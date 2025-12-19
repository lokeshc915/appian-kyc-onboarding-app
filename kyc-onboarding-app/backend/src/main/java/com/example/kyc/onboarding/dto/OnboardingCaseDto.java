package com.example.kyc.onboarding.dto;

import com.example.kyc.onboarding.DocumentType;
import com.example.kyc.onboarding.OnboardingCaseStatus;
import com.example.kyc.sla.SlaState;

import java.time.OffsetDateTime;
import java.util.List;

public record OnboardingCaseDto(
    Long id,
    OnboardingCaseStatus status,
    OffsetDateTime dueAt,
    SlaState slaState,
    OffsetDateTime slaBreachedAt,
    AccountDetailsDto accountDetails,
    List<DocumentDto> documents,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt
) {
  public record AccountDetailsDto(
      String firstName,
      String lastName,
      String phone,
      String accountType,
      String addressLine1,
      String city,
      String state,
      String zip
  ) {}

  public record DocumentDto(
      Long id,
      DocumentType type,
      String originalFileName,
      long sizeBytes,
      OffsetDateTime uploadedAt
  ) {}
}
