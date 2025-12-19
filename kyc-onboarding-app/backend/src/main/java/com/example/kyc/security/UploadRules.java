
package com.example.kyc.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class UploadRules {

  @Value("${app.upload.max-bytes:26214400}")
  private long maxBytes;

  @Value("${app.upload.allowed-content-types:application/pdf,image/jpeg,image/png}")
  private String allowedContentTypesCsv;

  public long maxBytes() { return maxBytes; }

  public Set<String> allowedContentTypes() {
    return Set.of(allowedContentTypesCsv.split("\\s*,\\s*"));
  }
}
