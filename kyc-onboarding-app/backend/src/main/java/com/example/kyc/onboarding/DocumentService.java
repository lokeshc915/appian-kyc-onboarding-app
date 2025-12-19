package com.example.kyc.onboarding;

import com.example.kyc.audit.AuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentService {

  private final OnboardingCaseService caseService;
  private final DocumentRecordRepository docRepo;
  private final FileStorageService storage;
  private final AuditService audit;

  @Transactional
  public DocumentRecord upload(Long caseId, DocumentType type, MultipartFile file, Authentication auth) {
    OnboardingCase c = caseService.getAuthorizedCase(caseId, auth);

    if (c.getStatus() == OnboardingCaseStatus.DRAFT_STEP1) {
      throw new IllegalArgumentException("Complete step1 before uploading documents");
    }

    var stored = storage.store(caseId, file);
    DocumentRecord doc = DocumentRecord.builder()
        .onboardingCase(c)
        .type(type)
        .originalFileName(stored.originalFileName())
        .storedPath(stored.storedPath())
        .sizeBytes(stored.sizeBytes())
        .build();
    c.getDocuments().add(doc);
    DocumentRecord saved = docRepo.save(doc);
    audit.documentUploaded(c, saved, auth);
    return saved;
  }

  @Transactional(readOnly = true)
  public List<DocumentRecord> list(Long caseId, Authentication auth) {
    caseService.getAuthorizedCase(caseId, auth);
    return docRepo.findByOnboardingCase_Id(caseId);
  }

  @Transactional(readOnly = true)
  public Downloadable download(Long docId, Authentication auth) {
    DocumentRecord doc = docRepo.findById(docId).orElseThrow(() -> new IllegalArgumentException("Doc not found"));
    caseService.getAuthorizedCase(doc.getOnboardingCase().getId(), auth);

    Path p = storage.load(doc.getStoredPath());
    if (!Files.exists(p)) throw new IllegalArgumentException("File missing on disk");
    return new Downloadable(doc.getOriginalFileName(), p);
  }

  @Transactional
  public void delete(Long docId, Authentication auth) {
    DocumentRecord doc = docRepo.findById(docId).orElseThrow(() -> new IllegalArgumentException("Doc not found"));
    caseService.getAuthorizedCase(doc.getOnboardingCase().getId(), auth);

    audit.documentDeleted(doc.getOnboardingCase(), doc, auth);

    try { Files.deleteIfExists(storage.load(doc.getStoredPath())); } catch (Exception ignored) {}
    docRepo.delete(doc);
  }

  public record Downloadable(String filename, Path path) {}
}
