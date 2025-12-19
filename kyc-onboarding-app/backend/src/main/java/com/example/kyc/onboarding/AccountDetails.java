package com.example.kyc.onboarding;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Embeddable
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class AccountDetails {

  @NotBlank
  private String firstName;

  @NotBlank
  private String lastName;

  private String phone;

  @NotBlank
  private String accountType; // e.g., Savings/Checking/Business

  private String addressLine1;
  private String city;
  private String state;
  private String zip;

  @jakarta.validation.constraints.NotBlank
  private String country;

  @jakarta.validation.constraints.NotBlank
  private String riskLevel; // LOW/MEDIUM/HIGH

}
