package com.example.kyc.admin;

import com.example.kyc.admin.dto.AdminStatsDto;
import com.example.kyc.onboarding.DocumentRecordRepository;
import com.example.kyc.onboarding.DocumentType;
import com.example.kyc.onboarding.OnboardingCaseRepository;
import com.example.kyc.onboarding.OnboardingCaseStatus;
import com.example.kyc.sla.SlaState;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumMap;

@Service
@RequiredArgsConstructor
public class AdminService {

  private final OnboardingCaseRepository caseRepo;
  private final DocumentRecordRepository docRepo;

  @Transactional(readOnly = true)
  public AdminStatsDto stats() {
    long total = caseRepo.count();

    EnumMap<OnboardingCaseStatus, Long> byStatus = new EnumMap<>(OnboardingCaseStatus.class);
    for (OnboardingCaseStatus s : OnboardingCaseStatus.values()) {
      byStatus.put(s, caseRepo.countByStatus(s));
    }

    long slaBreached = caseRepo.countBySlaState(SlaState.BREACHED);

    EnumMap<DocumentType, Long> docsByType = new EnumMap<>(DocumentType.class);
    for (DocumentType t : DocumentType.values()) {
      docsByType.put(t, docRepo.countByType(t));
    }

    return new AdminStatsDto(total, byStatus, slaBreached, docsByType);
  }
}
