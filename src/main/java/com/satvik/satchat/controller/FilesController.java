package com.satvik.satchat.controller;

import com.satvik.satchat.entity.FileInfo;
import com.satvik.satchat.message.ResponseMessage;
import com.satvik.satchat.service.IFilesStorageService;
import java.util.List;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

@Controller
public class FilesController {
  private final IFilesStorageService storageService;
  public FilesController(IFilesStorageService storageService) {
    this.storageService = storageService;
  }

  @PostMapping("/upload")
  public ResponseEntity<ResponseMessage> uploadFile(@RequestParam("file") MultipartFile file) {
    String message = "";
    try {
      String originalFilename = file.getOriginalFilename();
      String extension = "";
      if (originalFilename == null || originalFilename.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ResponseMessage("File name is empty"));
      }
      int dotIndex = originalFilename.lastIndexOf('.');
      if (dotIndex > 0) {
        extension = originalFilename.substring(dotIndex);
        originalFilename = originalFilename.substring(0, dotIndex);
      }
      String timestamp = String.valueOf(System.currentTimeMillis());
      String newFilename = originalFilename + "_" + timestamp + extension;
      //            rename the file to newFilename

      storageService.save(file, newFilename);

      Resource path = storageService.load(newFilename);
      String url =
          MvcUriComponentsBuilder.fromMethodName(
                  FilesController.class, "getFile", path.getFilename())
              .build()
              .toString();

      return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(url));
    } catch (Exception e) {
      message =
          "Could not upload the file: " + file.getOriginalFilename() + ". Error: " + e.getMessage();
      return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED)
          .body(new ResponseMessage(message));
    }
  }

  @GetMapping("/files")
  public ResponseEntity<List<FileInfo>> getListFiles() {
    List<FileInfo> fileInfos =
        storageService
            .loadAll()
            .map(
                path -> {
                  String filename = path.getFileName().toString();
                  String url =
                      MvcUriComponentsBuilder.fromMethodName(
                              FilesController.class, "getFile", path.getFileName().toString())
                          .build()
                          .toString();

                  return new FileInfo(filename, url);
                })
            .toList();

    return ResponseEntity.status(HttpStatus.OK).body(fileInfos);
  }

  @GetMapping("/files/{filename:.+}")
  public ResponseEntity<Resource> getFile(@PathVariable String filename) {
    Resource file = storageService.load(filename);
    return ResponseEntity.ok()
        .header(
            HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
        .body(file);
  }

  @DeleteMapping("/files/{filename:.+}")
  public ResponseEntity<ResponseMessage> deleteFile(@PathVariable String filename) {
    String message = "";

    try {
      boolean existed = storageService.delete(filename);

      if (existed) {
        message = "Delete the file successfully: " + filename;
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
      }

      message = "The file does not exist!";
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseMessage(message));
    } catch (Exception e) {
      message = "Could not delete the file: " + filename + ". Error: " + e.getMessage();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(new ResponseMessage(message));
    }
  }
}
