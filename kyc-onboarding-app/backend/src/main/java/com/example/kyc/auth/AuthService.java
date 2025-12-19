package com.example.kyc.auth;

import com.example.kyc.auth.dto.LoginRequest;
import com.example.kyc.auth.dto.RegisterRequest;
import com.example.kyc.security.JwtService;
import com.example.kyc.security.UserPrincipal;
import com.example.kyc.user.Role;
import com.example.kyc.user.User;
import com.example.kyc.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;
  private final JwtService jwtService;

  public User register(RegisterRequest req) {
    if (userRepository.existsByUsername(req.username())) {
      throw new IllegalArgumentException("Username already exists");
    }
    User user = User.builder()
        .username(req.username())
        .email(req.email())
        .passwordHash(passwordEncoder.encode(req.password()))
        .roles(Set.of(Role.ROLE_REQUESTER))
        .build();
    return userRepository.save(user);
  }

  public String login(LoginRequest req) {
    Authentication auth = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(req.username(), req.password())
    );
    UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
    return jwtService.createAccessToken(principal);
  }
}
