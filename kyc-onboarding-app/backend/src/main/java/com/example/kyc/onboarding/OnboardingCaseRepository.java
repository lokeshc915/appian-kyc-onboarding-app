package com.example.kyc.onboarding;

import com.example.kyc.sla.SlaState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.List;

public interface OnboardingCaseRepository extends JpaRepository<OnboardingCase, Long> {
  List<OnboardingCase> findByCreatedBy_Id(Long userId);

  List<OnboardingCase> findByDueAtBeforeAndSlaStateAndStatusIn(
      OffsetDateTime now,
      SlaState slaState,
      List<OnboardingCaseStatus> statuses
  );

  long countByStatus(OnboardingCaseStatus status);
  long countBySlaState(SlaState slaState);
}
