package com.satvik.satchat.service.implement;

import com.satvik.satchat.service.IFilesStorageService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.util.stream.Stream;

@Service
public class FilesStorageServiceImpl implements IFilesStorageService {

  private final Path root = Paths.get("uploads");

  @Override
  public void init() {
    try {
      Files.createDirectories(root);
    } catch (IOException e) {
      throw new RuntimeException("Could not initialize folder for upload!");
    }
  }

  @Override
  public void save(MultipartFile file, String filename) {
    try {
      Path destination = root.resolve(filename);
      Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
    } catch (Exception e) {
      if (e instanceof FileAlreadyExistsException) {
        throw new RuntimeException("A file of that name already exists.");
      }

      throw new RuntimeException(e.getMessage());
    }
  }

  @Override
  public Resource load(String filename) {
    try {
      Path file = root.resolve(filename);
      Resource resource = new UrlResource(file.toUri());

      if (resource.exists() || resource.isReadable()) {
        return resource;
      } else {
        throw new RuntimeException("Could not read the file!");
      }
    } catch (MalformedURLException e) {
      throw new RuntimeException("Error: " + e.getMessage());
    }
  }

  @Override
  public boolean delete(String filename) {
    try {
      Path file = root.resolve(filename);
      return Files.deleteIfExists(file);
    } catch (IOException e) {
      throw new RuntimeException("Error: " + e.getMessage());
    }
  }

  @Override
  public void deleteAll() {
    FileSystemUtils.deleteRecursively(root.toFile());
  }

  @Override
  public Stream<Path> loadAll() {
    try {
      return Files.walk(this.root, 1).filter(path -> !path.equals(this.root)).map(this.root::relativize);
    } catch (IOException e) {
      throw new RuntimeException("Could not load the files!");
    }
  }

}
