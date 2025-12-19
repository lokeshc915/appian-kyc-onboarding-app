package com.example.kyc.sla;

/**
 * Appian timers equivalent: each case has a due date; when it passes, the SLA is considered breached.
 */
public enum SlaState {
  ON_TRACK,
  BREACHED
}
