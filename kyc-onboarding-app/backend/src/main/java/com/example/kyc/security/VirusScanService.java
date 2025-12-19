
package com.example.kyc.security;

import org.springframework.web.multipart.MultipartFile;

public interface VirusScanService {
  void scanOrThrow(MultipartFile file) throws Exception;
}
