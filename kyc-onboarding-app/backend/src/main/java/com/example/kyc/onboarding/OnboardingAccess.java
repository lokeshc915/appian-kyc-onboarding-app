package com.example.kyc.onboarding;

import com.example.kyc.security.JwtPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class OnboardingAccess {

  public Long currentUserId(Authentication authentication) {
    Object p = authentication.getPrincipal();
    if (p instanceof JwtPrincipal jp) return jp.id();
    return null;
  }

  public boolean isApprover(Authentication authentication) {
    return authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_APPROVER"));
  }

  public boolean isAdmin(Authentication authentication) {
    return authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
  }

  /** Approver/Admin can access any case (mirrors "privileged users" access). */
  public boolean isPrivileged(Authentication authentication) {
    return isApprover(authentication) || isAdmin(authentication);
  }
}
