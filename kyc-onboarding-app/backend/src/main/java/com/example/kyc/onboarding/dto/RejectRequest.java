package com.example.kyc.onboarding.dto;

import jakarta.validation.constraints.NotBlank;

public record RejectRequest(
    @NotBlank String reason
) {
}
