package com.example.kyc.onboarding;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name="document_record")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class DocumentRecord {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  private OnboardingCase onboardingCase;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private DocumentType type;

  @Column(nullable = false)
  private String originalFileName;

  @Column(nullable = false)
  private String storedPath;

  @Column(nullable = false)
  private long sizeBytes;

  private OffsetDateTime uploadedAt;

  @PrePersist
  void onCreate() {
    uploadedAt = OffsetDateTime.now();
  }
}
