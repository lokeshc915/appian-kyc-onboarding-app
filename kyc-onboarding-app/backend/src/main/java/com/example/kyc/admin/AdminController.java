package com.example.kyc.admin;

import com.example.kyc.admin.dto.AdminStatsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

  private final AdminService service;

  @GetMapping("/stats")
  @PreAuthorize("hasAuthority('ROLE_ADMIN')")
  public AdminStatsDto stats() {
    return service.stats();
  }
}
