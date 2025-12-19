
package com.example.kyc.security;

import com.example.kyc.onboarding.OnboardingCase;
import com.example.kyc.user.Role;
import com.example.kyc.user.User;
import org.springframework.stereotype.Service;

@Service
public class DocumentAccessService {

  private final AbacConfig abac;

  public DocumentAccessService(AbacConfig abac) {
    this.abac = abac;
  }

  public void assertCanDownload(User actor, OnboardingCase caze) {
    if (actor.getRoles().contains(Role.ROLE_ADMIN)) return;

    if (!actor.getRoles().contains(Role.ROLE_REVIEWER)) {
      throw new SecurityException("Not allowed to download documents");
    }

    String country = caze.getAccountDetails() != null ? caze.getAccountDetails().getCountry() : null;
    String risk = caze.getAccountDetails() != null ? caze.getAccountDetails().getRiskLevel() : null;

    if (abac.getAllowedCountries() != null && !abac.getAllowedCountries().isEmpty()) {
      boolean ok = country != null && abac.getAllowedCountries().stream().anyMatch(x -> x.equalsIgnoreCase(country));
      if (!ok) throw new SecurityException("ABAC denied: country not allowed");
    }

    if (risk != null && isRiskHigherThan(risk, abac.getMaxRiskLevel())) {
      throw new SecurityException("ABAC denied: risk too high");
    }
  }

  private boolean isRiskHigherThan(String risk, String max) {
    return rank(risk) > rank(max);
  }

  private int rank(String r) {
    if (r == null) return 0;
    return switch (r.toUpperCase()) {
      case "LOW" -> 1;
      case "MEDIUM" -> 2;
      case "HIGH" -> 3;
      default -> 2;
    };
  }
}
