
package com.example.kyc.storage;

import com.azure.storage.blob.*;
import com.azure.storage.blob.sas.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.OffsetDateTime;

@Component
@Profile("azure")
public class AzureSignedUrlService implements SignedUrlService {

  private final BlobServiceClient serviceClient;

  @Value("${app.storage.azure.container}")
  private String container;

  public AzureSignedUrlService(@Value("${app.storage.azure.connection-string}") String connectionString) {
    this.serviceClient = new BlobServiceClientBuilder().connectionString(connectionString).buildClient();
  }

  @Override
  public String createDownloadUrl(String objectKey, Duration ttl) {
    BlobContainerClient containerClient = serviceClient.getBlobContainerClient(container);
    BlobClient blob = containerClient.getBlobClient(objectKey);

    BlobSasPermission perm = new BlobSasPermission().setReadPermission(true);
    BlobServiceSasSignatureValues values = new BlobServiceSasSignatureValues(
        OffsetDateTime.now().plusSeconds(ttl.toSeconds()), perm);

    String sas = blob.generateSas(values);
    return blob.getBlobUrl() + "?" + sas;
  }
}
