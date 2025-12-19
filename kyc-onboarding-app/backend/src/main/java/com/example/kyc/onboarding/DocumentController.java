package com.example.kyc.onboarding;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ContentDisposition;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class DocumentController {

  private final DocumentService documentService;

  @PostMapping(value = "/api/cases/{caseId}/documents", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @PreAuthorize("hasAuthority('ROLE_REQUESTER')")
  public ResponseEntity<?> upload(
      @PathVariable Long caseId,
      @RequestParam DocumentType type,
      @RequestPart("file") MultipartFile file,
      Authentication auth
  ) {
    var doc = documentService.upload(caseId, type, file, auth);
    return ResponseEntity.ok().body(java.util.Map.of("id", doc.getId()));
  }

  @GetMapping("/api/cases/{caseId}/documents")
  @PreAuthorize("hasAnyAuthority('ROLE_REQUESTER','ROLE_APPROVER','ROLE_ADMIN')")
  public List<?> list(@PathVariable Long caseId, Authentication auth) {
    return documentService.list(caseId, auth).stream().map(d -> java.util.Map.of(
        "id", d.getId(),
        "type", d.getType(),
        "originalFileName", d.getOriginalFileName(),
        "sizeBytes", d.getSizeBytes(),
        "uploadedAt", d.getUploadedAt()
    )).toList();
  }

  @GetMapping("/api/documents/{docId}/download")
  @PreAuthorize("hasAnyAuthority('ROLE_REQUESTER','ROLE_APPROVER','ROLE_ADMIN')")
  public ResponseEntity<FileSystemResource> download(@PathVariable Long docId, Authentication auth) {
    var dl = documentService.download(docId, auth);
    FileSystemResource res = new FileSystemResource(dl.path());
    String safeName = dl.filename().replace("\r", " ").replace("\n", " ");
    String contentDisposition = ContentDisposition.attachment().filename(safeName).build().toString();
    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
        .contentType(MediaType.APPLICATION_OCTET_STREAM)
        .body(res);
  }

  @DeleteMapping("/api/documents/{docId}")
  @PreAuthorize("hasAnyAuthority('ROLE_REQUESTER','ROLE_ADMIN')")
  public ResponseEntity<?> delete(@PathVariable Long docId, Authentication auth) {
    documentService.delete(docId, auth);
    return ResponseEntity.noContent().build();
  }
}
