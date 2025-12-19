package com.example.kyc.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record RegisterRequest(
    @NotBlank String username,
    @NotBlank String password,
    String email
) {}
