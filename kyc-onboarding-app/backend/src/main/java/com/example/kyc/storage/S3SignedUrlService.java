
package com.example.kyc.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.time.Duration;

@Component
@Profile("s3")
public class S3SignedUrlService implements SignedUrlService {

  private final S3Presigner presigner = S3Presigner.create();

  @Value("${app.storage.s3.bucket}")
  private String bucket;

  @Override
  public String createDownloadUrl(String objectKey, Duration ttl) {
    GetObjectRequest get = GetObjectRequest.builder().bucket(bucket).key(objectKey).build();
    GetObjectPresignRequest presign = GetObjectPresignRequest.builder()
        .signatureDuration(ttl)
        .getObjectRequest(get)
        .build();
    return presigner.presignGetObject(presign).url().toString();
  }
}
