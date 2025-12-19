
package com.example.kyc.security;

import io.github.bucket4j.Bucket;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class RateLimitFilter implements Filter {

  private final RateLimitKeyResolver keyResolver;
  private final RateLimitPolicies policies;
  private final RedisBucketProvider buckets;

  public RateLimitFilter(RateLimitKeyResolver keyResolver, RateLimitPolicies policies, RedisBucketProvider buckets) {
    this.keyResolver = keyResolver;
    this.policies = policies;
    this.buckets = buckets;
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {

    HttpServletRequest req = (HttpServletRequest) request;

    String path = req.getRequestURI();
    if (path.startsWith("/swagger") || path.startsWith("/v3/api-docs") || path.startsWith("/actuator")) {
      chain.doFilter(request, response);
      return;
    }

    String key = keyResolver.resolve(req) + "|ep:" + path;
    RateLimitPolicy policy = policies.resolve(req);
    Bucket bucket = buckets.resolveBucket(key, policy);

    if (bucket.tryConsume(1)) {
      chain.doFilter(request, response);
      return;
    }

    HttpServletResponse res = (HttpServletResponse) response;
    res.setStatus(429);
    res.setContentType("text/plain");
    res.getWriter().write("Too many requests");
  }
}
