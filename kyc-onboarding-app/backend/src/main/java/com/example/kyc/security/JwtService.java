package com.example.kyc.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class JwtService {

  @Value("${app.security.jwt.secret}")
  private String secret;

  @Value("${app.security.jwt.issuer}")
  private String issuer;

  @Value("${app.security.jwt.access-token-minutes:60}")
  private long accessTokenMinutes;

  private SecretKey key() {
    return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
  }

  private Claims claims(String token) {
    return Jwts.parser().verifyWith(key()).build()
        .parseSignedClaims(token).getPayload();
  }

  public String createAccessToken(UserPrincipal principal) {
    Instant now = Instant.now();
    Instant exp = now.plus(accessTokenMinutes, ChronoUnit.MINUTES);

    return Jwts.builder()
        .issuer(issuer)
        .subject(principal.getId().toString())
        .claims(Map.of(
            "username", principal.getUsername(),
            "roles", principal.getAuthorities().stream().map(a -> a.getAuthority()).toList()
        ))
        .issuedAt(Date.from(now))
        .expiration(Date.from(exp))
        .signWith(key())
        .compact();
  }

  public Long parseUserId(String token) {
    return Long.valueOf(claims(token).getSubject());
  }

  public String parseUsername(String token) {
    return claims(token).get("username", String.class);
  }

  @SuppressWarnings("unchecked")
  public List<String> parseRoles(String token) {
    Object raw = claims(token).get("roles");
    if (raw instanceof List<?> list) {
      return list.stream().map(String::valueOf).toList();
    }
    return List.of();
  }

  public boolean isValid(String token) {
    try {
      claims(token);
      return true;
    } catch (Exception e) {
      return false;
    }
  }
}
