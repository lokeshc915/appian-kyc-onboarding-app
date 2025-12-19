package com.example.kyc.storage;

import java.io.InputStream;
import java.nio.file.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile({"default","docker","local"})
public class LocalFsBlobStorage implements BlobStorage {

  @Value("${app.storage.base-dir}")
  private String baseDir;

  @Override
  public StoredObject save(String key, InputStream stream, long sizeBytes, String contentType) throws Exception {
    Path target = Paths.get(baseDir).resolve(key).normalize();
    Files.createDirectories(target.getParent());
    Files.copy(stream, target, StandardCopyOption.REPLACE_EXISTING);
    return new StoredObject(key, target.toString());
  }

  @Override
  public InputStream read(String key) throws Exception {
    Path target = Paths.get(baseDir).resolve(key).normalize();
    return Files.newInputStream(target, StandardOpenOption.READ);
  }

  @Override
  public void delete(String key) throws Exception {
    Path target = Paths.get(baseDir).resolve(key).normalize();
    Files.deleteIfExists(target);
  }
}
