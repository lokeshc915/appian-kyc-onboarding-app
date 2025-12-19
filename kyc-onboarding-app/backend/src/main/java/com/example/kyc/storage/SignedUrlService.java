
package com.example.kyc.storage;

import java.time.Duration;

public interface SignedUrlService {
  String createDownloadUrl(String objectKey, Duration ttl) throws Exception;
}
