package com.example.kyc.auth;

import com.example.kyc.auth.dto.AuthResponse;
import com.example.kyc.auth.dto.LoginRequest;
import com.example.kyc.auth.dto.RegisterRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;

  @PostMapping("/register")
  public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest req) {
    authService.register(req);
    return ResponseEntity.ok().build();
  }

  @PostMapping("/login")
  public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest req) {
    String token = authService.login(req);
    return ResponseEntity.ok(new AuthResponse(token));
  }
}
