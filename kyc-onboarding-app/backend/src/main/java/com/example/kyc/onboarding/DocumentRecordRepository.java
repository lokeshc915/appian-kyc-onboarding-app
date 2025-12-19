package com.example.kyc.onboarding;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentRecordRepository extends JpaRepository<DocumentRecord, Long> {
  List<DocumentRecord> findByOnboardingCase_Id(Long caseId);

  long countByType(DocumentType type);
}
