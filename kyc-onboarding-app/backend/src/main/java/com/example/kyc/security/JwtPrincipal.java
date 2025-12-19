package com.example.kyc.security;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * Minimal principal for JWT-authenticated requests.
 */
public record JwtPrincipal(Long id, String username, Collection<? extends GrantedAuthority> authorities) { }
