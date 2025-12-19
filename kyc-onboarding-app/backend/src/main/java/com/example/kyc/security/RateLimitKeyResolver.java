
package com.example.kyc.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class RateLimitKeyResolver {
  public String resolve(HttpServletRequest req) {
    String user = req.getUserPrincipal() != null ? req.getUserPrincipal().getName() : null;
    return (user != null ? "u:" + user : "ip:" + req.getRemoteAddr());
  }
}
