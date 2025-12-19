package com.example.kyc.sla;

import com.example.kyc.onboarding.OnboardingCaseStatus;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Component
public class SlaPolicy {

  private final SlaProperties props;

  public SlaPolicy(SlaProperties props) {
    this.props = props;
  }

  /**
   * Calculates the due date for the current status.
   * This mimics Appian timers without any BPM engine.
   */
  public OffsetDateTime dueAtFor(OnboardingCaseStatus status, OffsetDateTime from) {
    if (from == null) {
      from = OffsetDateTime.now();
    }
    return switch (status) {
      case DRAFT_STEP1 -> from.plusHours(props.step1Hours());
      case DRAFT_STEP2 -> from.plusHours(props.step2Hours());
      case SUBMITTED, IN_REVIEW -> from.plusHours(props.reviewHours());
      case APPROVED, REJECTED -> null; // closed
    };
  }
}
