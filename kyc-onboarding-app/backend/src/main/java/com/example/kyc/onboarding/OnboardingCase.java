package com.example.kyc.onboarding;

import com.example.kyc.user.User;
import com.example.kyc.sla.SlaState;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="onboarding_case")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class OnboardingCase {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  private User createdBy;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private OnboardingCaseStatus status;

  /**
   * Current step due date (Appian timer equivalent). Updated on each status transition.
   */
  private OffsetDateTime dueAt;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private SlaState slaState;

  private OffsetDateTime slaBreachedAt;

  @Embedded
  private AccountDetails accountDetails;

  private OffsetDateTime createdAt;
  private OffsetDateTime updatedAt;

  @OneToMany(mappedBy="onboardingCase", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private List<DocumentRecord> documents = new ArrayList<>();

  @PrePersist
  void onCreate() {
    createdAt = OffsetDateTime.now();
    updatedAt = createdAt;
    if (slaState == null) {
      slaState = SlaState.ON_TRACK;
    }
  }

  @PreUpdate
  void onUpdate() {
    updatedAt = OffsetDateTime.now();
  }
}
