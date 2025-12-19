package com.example.kyc.storage;

import java.io.InputStream;

public interface BlobStorage {
  StoredObject save(String key, InputStream stream, long sizeBytes, String contentType) throws Exception;
  InputStream read(String key) throws Exception;
  void delete(String key) throws Exception;
}
