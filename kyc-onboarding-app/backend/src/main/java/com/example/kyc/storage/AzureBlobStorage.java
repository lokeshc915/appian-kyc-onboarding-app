package com.example.kyc.storage;

import java.io.InputStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import com.azure.storage.blob.*;
import com.azure.storage.blob.models.*;

@Component
@Profile("azure")
public class AzureBlobStorage implements BlobStorage {

  private final BlobServiceClient serviceClient;

  @Value("${app.storage.azure.container}")
  private String container;

  @Value("${app.storage.azure.prefix:}")
  private String prefix;

  public AzureBlobStorage(@Value("${app.storage.azure.connection-string}") String connectionString) {
    this.serviceClient = new BlobServiceClientBuilder().connectionString(connectionString).buildClient();
  }

  @Override
  public StoredObject save(String key, InputStream stream, long sizeBytes, String contentType) throws Exception {
    String blobName = (prefix == null ? "" : prefix) + key;
    BlobContainerClient containerClient = serviceClient.getBlobContainerClient(container);
    containerClient.createIfNotExists();
    BlobClient blob = containerClient.getBlobClient(blobName);
    blob.upload(stream, sizeBytes, true);
    if (contentType != null) {
      blob.setHttpHeaders(new BlobHttpHeaders().setContentType(contentType));
    }
    return new StoredObject(blobName, blob.getBlobUrl());
  }

  @Override
  public InputStream read(String key) throws Exception {
    String blobName = (prefix == null ? "" : prefix) + key;
    BlobContainerClient containerClient = serviceClient.getBlobContainerClient(container);
    return containerClient.getBlobClient(blobName).openInputStream();
  }

  @Override
  public void delete(String key) throws Exception {
    String blobName = (prefix == null ? "" : prefix) + key;
    BlobContainerClient containerClient = serviceClient.getBlobContainerClient(container);
    containerClient.getBlobClient(blobName).deleteIfExists();
  }
}
