package com.example.kyc.admin.dto;

import com.example.kyc.onboarding.OnboardingCaseStatus;
import com.example.kyc.onboarding.DocumentType;

import java.util.Map;

public record AdminStatsDto(
    long totalCases,
    Map<OnboardingCaseStatus, Long> casesByStatus,
    long slaBreachedCases,
    Map<DocumentType, Long> documentsByType
) {
}
