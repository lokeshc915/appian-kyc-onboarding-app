package com.example.kyc.audit;

import com.example.kyc.onboarding.OnboardingCase;
import com.example.kyc.onboarding.OnboardingCaseStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "audit_event", indexes = {
    @Index(name = "idx_audit_case", columnList = "case_id,eventAt")
})
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class AuditEvent {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "case_id", nullable = false)
  private OnboardingCase onboardingCase;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private AuditAction action;

  private String actor;
  private Long actorUserId;

  @Enumerated(EnumType.STRING)
  private OnboardingCaseStatus fromStatus;
  @Enumerated(EnumType.STRING)
  private OnboardingCaseStatus toStatus;

  @Column(length = 500)
  private String message;

  @Lob
  private String metadataJson;

  @Column(nullable = false)
  private OffsetDateTime eventAt;

  @PrePersist
  void onCreate() {
    if (eventAt == null) eventAt = OffsetDateTime.now();
  }
}
