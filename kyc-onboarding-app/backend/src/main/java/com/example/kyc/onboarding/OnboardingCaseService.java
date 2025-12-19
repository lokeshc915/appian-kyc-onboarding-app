package com.example.kyc.onboarding;

import com.example.kyc.audit.AuditService;
import com.example.kyc.onboarding.dto.AccountDetailsRequest;
import com.example.kyc.onboarding.dto.OnboardingCaseDto;
import com.example.kyc.sla.SlaPolicy;
import com.example.kyc.sla.SlaState;
import com.example.kyc.user.User;
import com.example.kyc.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OnboardingCaseService {

  private final OnboardingCaseRepository caseRepo;
  private final UserRepository userRepo;
  private final OnboardingAccess access;
  private final SlaPolicy slaPolicy;
  private final AuditService audit;

  @Transactional
  public OnboardingCase createCase(Authentication auth) {
    Long userId = access.currentUserId(auth);
    User user = userRepo.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
    OffsetDateTime now = OffsetDateTime.now();
    OnboardingCase c = OnboardingCase.builder()
        .createdBy(user)
        .status(OnboardingCaseStatus.DRAFT_STEP1)
        .slaState(SlaState.ON_TRACK)
        .dueAt(slaPolicy.dueAtFor(OnboardingCaseStatus.DRAFT_STEP1, now))
        .build();
    OnboardingCase saved = caseRepo.save(c);
    audit.caseCreated(saved, auth);
    return saved;
  }

  @Transactional
  public OnboardingCase saveStep1(Long caseId, AccountDetailsRequest req, Authentication auth) {
    OnboardingCase c = getAuthorizedCase(caseId, auth);
    if (c.getStatus() == OnboardingCaseStatus.SUBMITTED || c.getStatus() == OnboardingCaseStatus.IN_REVIEW
        || c.getStatus() == OnboardingCaseStatus.APPROVED || c.getStatus() == OnboardingCaseStatus.REJECTED) {
      throw new IllegalArgumentException("Cannot change Step 1 after case submission");
    }
    OnboardingCaseStatus from = c.getStatus();
    c.setAccountDetails(AccountDetails.builder()
        .firstName(req.firstName())
        .lastName(req.lastName())
        .phone(req.phone())
        .accountType(req.accountType())
        .addressLine1(req.addressLine1())
        .city(req.city())
        .state(req.state())
        .zip(req.zip())
        .build());
    c.setStatus(OnboardingCaseStatus.DRAFT_STEP2);
    resetSla(c);
    c.setDueAt(slaPolicy.dueAtFor(OnboardingCaseStatus.DRAFT_STEP2, OffsetDateTime.now()));
    audit.step1Saved(c, from, auth);
    return c;
  }

  @Transactional
  public OnboardingCase submit(Long caseId, Authentication auth) {
    OnboardingCase c = getAuthorizedCase(caseId, auth);
    if (c.getStatus() != OnboardingCaseStatus.DRAFT_STEP2) {
      throw new IllegalArgumentException("Case must be in Step 2 before submit");
    }
    validateRequiredDocs(c);
    OnboardingCaseStatus from = c.getStatus();
    c.setStatus(OnboardingCaseStatus.SUBMITTED);
    resetSla(c);
    c.setDueAt(slaPolicy.dueAtFor(OnboardingCaseStatus.SUBMITTED, OffsetDateTime.now()));
    audit.caseSubmitted(c, from, auth);
    return c;
  }

  @Transactional
  public OnboardingCase startReview(Long caseId, Authentication auth) {
    if (!access.isPrivileged(auth)) throw new IllegalArgumentException("Not allowed");
    OnboardingCase c = caseRepo.findById(caseId).orElseThrow(() -> new IllegalArgumentException("Case not found"));
    if (c.getStatus() != OnboardingCaseStatus.SUBMITTED) {
      throw new IllegalArgumentException("Only submitted cases can be moved to review");
    }
    OnboardingCaseStatus from = c.getStatus();
    c.setStatus(OnboardingCaseStatus.IN_REVIEW);
    resetSla(c);
    c.setDueAt(slaPolicy.dueAtFor(OnboardingCaseStatus.IN_REVIEW, OffsetDateTime.now()));
    audit.reviewStarted(c, from, auth);
    return c;
  }

  @Transactional
  public OnboardingCase approve(Long caseId, Authentication auth) {
    if (!access.isPrivileged(auth)) throw new IllegalArgumentException("Not allowed");
    OnboardingCase c = caseRepo.findById(caseId).orElseThrow(() -> new IllegalArgumentException("Case not found"));
    if (c.getStatus() != OnboardingCaseStatus.SUBMITTED && c.getStatus() != OnboardingCaseStatus.IN_REVIEW) {
      throw new IllegalArgumentException("Case is not ready to approve");
    }
    OnboardingCaseStatus from = c.getStatus();
    c.setStatus(OnboardingCaseStatus.APPROVED);
    c.setDueAt(null);
    audit.approved(c, from, auth);
    return c;
  }

  @Transactional
  public OnboardingCase reject(Long caseId, String reason, Authentication auth) {
    if (!access.isPrivileged(auth)) throw new IllegalArgumentException("Not allowed");
    OnboardingCase c = caseRepo.findById(caseId).orElseThrow(() -> new IllegalArgumentException("Case not found"));
    if (c.getStatus() != OnboardingCaseStatus.SUBMITTED && c.getStatus() != OnboardingCaseStatus.IN_REVIEW) {
      throw new IllegalArgumentException("Case is not ready to reject");
    }
    OnboardingCaseStatus from = c.getStatus();
    c.setStatus(OnboardingCaseStatus.REJECTED);
    c.setDueAt(null);
    audit.rejected(c, from, reason, auth);
    return c;
  }

  @Transactional(readOnly = true)
  public List<OnboardingCase> listMine(Authentication auth) {
    Long userId = access.currentUserId(auth);
    return caseRepo.findByCreatedBy_Id(userId);
  }

  @Transactional(readOnly = true)
  public List<OnboardingCase> listAll(Authentication auth) {
    if (!access.isPrivileged(auth)) throw new IllegalArgumentException("Not allowed");
    return caseRepo.findAll();
  }

  @Transactional(readOnly = true)
  public OnboardingCase getAuthorizedCase(Long caseId, Authentication auth) {
    OnboardingCase c = caseRepo.findById(caseId).orElseThrow(() -> new IllegalArgumentException("Case not found"));
    if (access.isPrivileged(auth)) return c;
    Long userId = access.currentUserId(auth);
    if (!c.getCreatedBy().getId().equals(userId)) {
      throw new IllegalArgumentException("Not allowed");
    }
    return c;
  }

  public OnboardingCaseDto toDto(OnboardingCase c) {
    var ad = c.getAccountDetails();
    var adDto = (ad == null) ? null : new OnboardingCaseDto.AccountDetailsDto(
        ad.getFirstName(), ad.getLastName(), ad.getPhone(), ad.getAccountType(),
        ad.getAddressLine1(), ad.getCity(), ad.getState(), ad.getZip()
    );
    var docs = c.getDocuments().stream().map(d ->
        new OnboardingCaseDto.DocumentDto(d.getId(), d.getType(), d.getOriginalFileName(), d.getSizeBytes(), d.getUploadedAt())
    ).toList();
    return new OnboardingCaseDto(
        c.getId(),
        c.getStatus(),
        c.getDueAt(),
        c.getSlaState(),
        c.getSlaBreachedAt(),
        adDto,
        docs,
        c.getCreatedAt(),
        c.getUpdatedAt()
    );
  }

  private void validateRequiredDocs(OnboardingCase c) {
    Map<DocumentType, Long> counts = c.getDocuments().stream()
        .collect(java.util.stream.Collectors.groupingBy(DocumentRecord::getType, java.util.stream.Collectors.counting()));

    for (DocumentType t : List.of(
        DocumentType.IDENTITY_DOCUMENT,
        DocumentType.FINANCIAL_DISCLOSURE_FORM,
        DocumentType.LETTER_OF_RECOMMENDATION
    )) {
      if (counts.getOrDefault(t, 0L) < 1L) {
        throw new IllegalArgumentException("Missing required document type: " + t);
      }
    }
  }

  private void resetSla(OnboardingCase c) {
    c.setSlaState(SlaState.ON_TRACK);
    c.setSlaBreachedAt(null);
  }
}
