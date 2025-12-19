package com.example.kyc.onboarding;

import com.example.kyc.audit.AuditService;
import com.example.kyc.audit.dto.AuditEventDto;
import com.example.kyc.onboarding.dto.AccountDetailsRequest;
import com.example.kyc.onboarding.dto.CreateCaseResponse;
import com.example.kyc.onboarding.dto.OnboardingCaseDto;
import com.example.kyc.onboarding.dto.RejectRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cases")
@RequiredArgsConstructor
public class OnboardingCaseController {

  private final OnboardingCaseService caseService;
  private final AuditService auditService;

  @PostMapping
  @PreAuthorize("hasAnyAuthority('ROLE_REQUESTER','ROLE_APPROVER','ROLE_ADMIN')")
  public CreateCaseResponse create(Authentication auth) {
    return new CreateCaseResponse(caseService.createCase(auth).getId());
  }

  @PutMapping("/{caseId}/step1")
  @PreAuthorize("hasAuthority('ROLE_REQUESTER')")
  public OnboardingCaseDto saveStep1(@PathVariable Long caseId, @Valid @RequestBody AccountDetailsRequest req, Authentication auth) {
    return caseService.toDto(caseService.saveStep1(caseId, req, auth));
  }

  @GetMapping("/{caseId}")
  @PreAuthorize("hasAnyAuthority('ROLE_REQUESTER','ROLE_APPROVER')")
  public OnboardingCaseDto get(@PathVariable Long caseId, Authentication auth) {
    return caseService.toDto(caseService.getAuthorizedCase(caseId, auth));
  }

  @GetMapping
  @PreAuthorize("hasAnyAuthority('ROLE_REQUESTER','ROLE_APPROVER','ROLE_ADMIN')")
  public List<OnboardingCaseDto> list(@RequestParam(defaultValue = "mine") String scope, Authentication auth) {
    var cases = "all".equalsIgnoreCase(scope) ? caseService.listAll(auth) : caseService.listMine(auth);
    return cases.stream().map(caseService::toDto).toList();
  }

  @PutMapping("/{caseId}/submit")
  @PreAuthorize("hasAuthority('ROLE_REQUESTER')")
  public OnboardingCaseDto submit(@PathVariable Long caseId, Authentication auth) {
    return caseService.toDto(caseService.submit(caseId, auth));
  }

  @PutMapping("/{caseId}/review/start")
  @PreAuthorize("hasAnyAuthority('ROLE_APPROVER','ROLE_ADMIN')")
  public OnboardingCaseDto startReview(@PathVariable Long caseId, Authentication auth) {
    return caseService.toDto(caseService.startReview(caseId, auth));
  }

  @PutMapping("/{caseId}/approve")
  @PreAuthorize("hasAnyAuthority('ROLE_APPROVER','ROLE_ADMIN')")
  public OnboardingCaseDto approve(@PathVariable Long caseId, Authentication auth) {
    return caseService.toDto(caseService.approve(caseId, auth));
  }

  @PutMapping("/{caseId}/reject")
  @PreAuthorize("hasAnyAuthority('ROLE_APPROVER','ROLE_ADMIN')")
  public OnboardingCaseDto reject(@PathVariable Long caseId, @Valid @RequestBody RejectRequest req, Authentication auth) {
    return caseService.toDto(caseService.reject(caseId, req.reason(), auth));
  }

  @GetMapping("/{caseId}/audit")
  @PreAuthorize("hasAnyAuthority('ROLE_REQUESTER','ROLE_APPROVER','ROLE_ADMIN')")
  public List<AuditEventDto> audit(@PathVariable Long caseId, Authentication auth) {
    // enforce case visibility
    caseService.getAuthorizedCase(caseId, auth);
    return auditService.listForCase(caseId);
  }
}
