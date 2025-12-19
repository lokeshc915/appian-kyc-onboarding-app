package com.example.kyc.audit.dto;

import com.example.kyc.audit.AuditAction;
import com.example.kyc.onboarding.OnboardingCaseStatus;

import java.time.OffsetDateTime;

public record AuditEventDto(
    Long id,
    AuditAction action,
    String actor,
    Long actorUserId,
    OnboardingCaseStatus fromStatus,
    OnboardingCaseStatus toStatus,
    String message,
    String metadataJson,
    OffsetDateTime eventAt
) {
}
