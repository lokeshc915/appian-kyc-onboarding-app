package com.example.kyc.onboarding;

import com.example.kyc.storage.BlobStorage;
import com.example.kyc.storage.StoredObject;
import java.io.InputStream;
import org.springframework.stereotype.Service;

@Service
public class FileStorageService {

  private final BlobStorage storage;

  public FileStorageService(BlobStorage storage) {
    this.storage = storage;
  }

  public StoredObject save(String key, InputStream stream, long sizeBytes, String contentType) throws Exception {
    return storage.save(key, stream, sizeBytes, contentType);
  }

  public InputStream read(String key) throws Exception {
    return storage.read(key);
  }

  public void delete(String key) throws Exception {
    storage.delete(key);
  }
}
