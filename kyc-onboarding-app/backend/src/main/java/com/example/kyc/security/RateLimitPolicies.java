
package com.example.kyc.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class RateLimitPolicies {

  public RateLimitPolicy resolve(HttpServletRequest req) {
    String path = req.getRequestURI();
    String method = req.getMethod();

    if (path.startsWith("/api/auth/login") || path.startsWith("/api/auth/register")) {
      return new RateLimitPolicy(10, Duration.ofMinutes(1));
    }
    if (path.contains("/documents") && "POST".equalsIgnoreCase(method)) {
      return new RateLimitPolicy(30, Duration.ofMinutes(1));
    }
    if (path.startsWith("/api/admin")) {
      return new RateLimitPolicy(60, Duration.ofMinutes(1));
    }
    return new RateLimitPolicy(100, Duration.ofMinutes(1));
  }
}
