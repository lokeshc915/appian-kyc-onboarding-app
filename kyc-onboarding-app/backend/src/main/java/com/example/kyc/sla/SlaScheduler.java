package com.example.kyc.sla;

import com.example.kyc.audit.AuditService;
import com.example.kyc.onboarding.OnboardingCase;
import com.example.kyc.onboarding.OnboardingCaseRepository;
import com.example.kyc.onboarding.OnboardingCaseStatus;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SlaScheduler {

  private static final Logger log = LoggerFactory.getLogger(SlaScheduler.class);

  private final OnboardingCaseRepository caseRepo;
  private final AuditService audit;
  private final SlaProperties props;

  @Scheduled(fixedDelayString = "${app.sla.check-interval-seconds:300}000")
  @Transactional
  public void markBreaches() {
    OffsetDateTime now = OffsetDateTime.now();
    List<OnboardingCaseStatus> tracked = List.of(
        OnboardingCaseStatus.DRAFT_STEP1,
        OnboardingCaseStatus.DRAFT_STEP2,
        OnboardingCaseStatus.SUBMITTED,
        OnboardingCaseStatus.IN_REVIEW
    );

    List<OnboardingCase> overdue = caseRepo.findByDueAtBeforeAndSlaStateAndStatusIn(
        now,
        SlaState.ON_TRACK,
        tracked
    );

    if (overdue.isEmpty()) return;

    for (OnboardingCase c : overdue) {
      c.setSlaState(SlaState.BREACHED);
      c.setSlaBreachedAt(now);
      audit.slaBreached(c, now);
      log.info("SLA breached for case {} (status={}, dueAt={})", c.getId(), c.getStatus(), c.getDueAt());
    }
  }
}
