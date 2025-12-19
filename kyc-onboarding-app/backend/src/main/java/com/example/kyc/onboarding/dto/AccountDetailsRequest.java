package com.example.kyc.onboarding.dto;

import jakarta.validation.constraints.NotBlank;

public record AccountDetailsRequest(
    @NotBlank String firstName,
    @NotBlank String lastName,
    String phone,
    @NotBlank String accountType,
    String addressLine1,
    String city,
    String state,
    String zip
) {}
