
package com.example.kyc.security;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Profile({"default","docker","local","s3","azure"})
public class NoOpVirusScanService implements VirusScanService {
  @Override
  public void scanOrThrow(MultipartFile file) {
    // Placeholder: integrate ClamAV/ICAP/vendor here.
  }
}
