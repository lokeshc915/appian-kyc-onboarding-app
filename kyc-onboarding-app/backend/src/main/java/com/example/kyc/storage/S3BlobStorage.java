package com.example.kyc.storage;

import java.io.InputStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

@Component
@Profile("s3")
public class S3BlobStorage implements BlobStorage {

  private final S3Client s3 = S3Client.builder().build();

  @Value("${app.storage.s3.bucket}")
  private String bucket;

  @Value("${app.storage.s3.prefix:}")
  private String prefix;

  @Override
  public StoredObject save(String key, InputStream stream, long sizeBytes, String contentType) throws Exception {
    String objectKey = (prefix == null ? "" : prefix) + key;
    PutObjectRequest req = PutObjectRequest.builder()
        .bucket(bucket)
        .key(objectKey)
        .contentType(contentType)
        .build();
    s3.putObject(req, RequestBody.fromInputStream(stream, sizeBytes));
    return new StoredObject(objectKey, "s3://" + bucket + "/" + objectKey);
  }

  @Override
  public InputStream read(String key) throws Exception {
    String objectKey = (prefix == null ? "" : prefix) + key;
    GetObjectRequest req = GetObjectRequest.builder().bucket(bucket).key(objectKey).build();
    return s3.getObject(req);
  }

  @Override
  public void delete(String key) throws Exception {
    String objectKey = (prefix == null ? "" : prefix) + key;
    DeleteObjectRequest req = DeleteObjectRequest.builder().bucket(bucket).key(objectKey).build();
    s3.deleteObject(req);
  }
}
