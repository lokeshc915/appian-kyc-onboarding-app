package com.example.kyc.audit;

import com.example.kyc.audit.dto.AuditEventDto;
import com.example.kyc.onboarding.DocumentRecord;
import com.example.kyc.onboarding.OnboardingCase;
import com.example.kyc.onboarding.OnboardingCaseStatus;
import com.example.kyc.security.JwtPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditService {

  private final AuditEventRepository repo;

  @Transactional
  public void caseCreated(OnboardingCase c, Authentication auth) {
    save(c, AuditAction.CASE_CREATED, auth, null, c.getStatus(), "Case created", null);
  }

  @Transactional
  public void step1Saved(OnboardingCase c, OnboardingCaseStatus from, Authentication auth) {
    save(c, AuditAction.STEP1_SAVED, auth, from, c.getStatus(), "Step 1 account details saved", null);
  }

  @Transactional
  public void documentUploaded(OnboardingCase c, DocumentRecord doc, Authentication auth) {
    String meta = String.format("{\"docId\":%d,\"type\":\"%s\",\"name\":\"%s\"}",
        doc.getId(), doc.getType(), safe(doc.getOriginalFileName()));
    save(c, AuditAction.DOCUMENT_UPLOADED, auth, c.getStatus(), c.getStatus(), "Document uploaded", meta);
  }

  @Transactional
  public void documentDeleted(OnboardingCase c, DocumentRecord doc, Authentication auth) {
    String meta = String.format("{\"docId\":%d,\"type\":\"%s\",\"name\":\"%s\"}",
        doc.getId(), doc.getType(), safe(doc.getOriginalFileName()));
    save(c, AuditAction.DOCUMENT_DELETED, auth, c.getStatus(), c.getStatus(), "Document deleted", meta);
  }

  @Transactional
  public void caseSubmitted(OnboardingCase c, OnboardingCaseStatus from, Authentication auth) {
    save(c, AuditAction.CASE_SUBMITTED, auth, from, c.getStatus(), "Case submitted for review", null);
  }

  @Transactional
  public void reviewStarted(OnboardingCase c, OnboardingCaseStatus from, Authentication auth) {
    save(c, AuditAction.CASE_REVIEW_STARTED, auth, from, c.getStatus(), "Review started", null);
  }

  @Transactional
  public void approved(OnboardingCase c, OnboardingCaseStatus from, Authentication auth) {
    save(c, AuditAction.CASE_APPROVED, auth, from, c.getStatus(), "Case approved", null);
  }

  @Transactional
  public void rejected(OnboardingCase c, OnboardingCaseStatus from, String reason, Authentication auth) {
    String meta = reason == null ? null : String.format("{\"reason\":\"%s\"}", safe(reason));
    save(c, AuditAction.CASE_REJECTED, auth, from, c.getStatus(), "Case rejected", meta);
  }

  @Transactional
  public void slaBreached(OnboardingCase c, OffsetDateTime at) {
    AuditEvent ev = AuditEvent.builder()
        .onboardingCase(c)
        .action(AuditAction.SLA_BREACHED)
        .actor("system")
        .fromStatus(c.getStatus())
        .toStatus(c.getStatus())
        .message("SLA breached")
        .eventAt(at)
        .build();
    repo.save(ev);
  }

  @Transactional(readOnly = true)
  public List<AuditEventDto> listForCase(Long caseId) {
    return repo.findByOnboardingCase_IdOrderByEventAtAsc(caseId).stream().map(this::toDto).toList();
  }

  private AuditEventDto toDto(AuditEvent e) {
    return new AuditEventDto(
        e.getId(),
        e.getAction(),
        e.getActor(),
        e.getActorUserId(),
        e.getFromStatus(),
        e.getToStatus(),
        e.getMessage(),
        e.getMetadataJson(),
        e.getEventAt()
    );
  }

  private void save(OnboardingCase c, AuditAction action, Authentication auth,
                    OnboardingCaseStatus from, OnboardingCaseStatus to, String message, String metaJson) {
    String actor = "unknown";
    Long actorId = null;
    if (auth != null && auth.getPrincipal() instanceof JwtPrincipal jp) {
      actor = jp.username();
      actorId = jp.id();
    }
    AuditEvent ev = AuditEvent.builder()
        .onboardingCase(c)
        .action(action)
        .actor(actor)
        .actorUserId(actorId)
        .fromStatus(from)
        .toStatus(to)
        .message(message)
        .metadataJson(metaJson)
        .build();
    repo.save(ev);
  }

  private static String safe(String s) {
    if (s == null) return "";
    return s.replace("\\", "\\\\")
        .replace("\"", "\\\"")
        .replace("\n", " ")
        .replace("\r", " ");
  }
}
