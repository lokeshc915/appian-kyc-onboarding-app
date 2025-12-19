package com.example.kyc.security;

import com.example.kyc.user.Role;
import com.example.kyc.user.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.stream.Collectors;

@Getter
public class UserPrincipal implements UserDetails {
  private final Long id;
  private final String username;
  private final String password;
  private final Collection<? extends GrantedAuthority> authorities;
  private final String email;

  public UserPrincipal(User user) {
    this.id = user.getId();
    this.username = user.getUsername();
    this.password = user.getPasswordHash();
    this.email = user.getEmail();
    this.authorities = user.getRoles().stream()
        .map(Role::name)
        .map(SimpleGrantedAuthority::new)
        .collect(Collectors.toSet());
  }

  @Override public Collection<? extends GrantedAuthority> getAuthorities() { return authorities; }
  @Override public String getPassword() { return password; }
  @Override public String getUsername() { return username; }
  @Override public boolean isAccountNonExpired() { return true; }
  @Override public boolean isAccountNonLocked() { return true; }
  @Override public boolean isCredentialsNonExpired() { return true; }
  @Override public boolean isEnabled() { return true; }
}
