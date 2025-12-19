package com.example.kyc.security;

import com.example.kyc.user.Role;
import com.example.kyc.user.User;
import com.example.kyc.user.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

  private final UserRepository userRepository;
  private final JwtService jwtService;

  @Value("${app.frontend.oauth2-redirect-url}")
  private String redirectUrl;

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
      throws IOException, ServletException {

    OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
    String email = oauthUser.getAttribute("email");
    String name = oauthUser.getAttribute("name");
    String username = (email != null) ? email : (name != null ? name.replaceAll("\\s+", "").toLowerCase() : "oauthuser");

    User user = userRepository.findByEmail(email).orElseGet(() -> {
      User created = User.builder()
          .username(username)
          .email(email)
          // passwordHash unused for oauth users
          .passwordHash("{noop}OAUTH2")
          .roles(Set.of(Role.ROLE_REQUESTER))
          .build();
      return userRepository.save(created);
    });

    UserPrincipal principal = new UserPrincipal(user);
    String token = jwtService.createAccessToken(principal);

    String target = redirectUrl + "?token=" + URLEncoder.encode(token, StandardCharsets.UTF_8);
    response.sendRedirect(target);
  }
}
