package com.fitech.app.users.application.controllers;

import com.fitech.app.users.application.exception.StorageFileNotFoundException;
import com.fitech.app.users.domain.entities.UserFiles;
import com.fitech.app.users.application.dto.UserFilesDto;
import com.fitech.app.users.domain.services.FileUploadService;
import com.fitech.app.users.infrastructure.repository.UserFileRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/v1/app/file-upload")
@Tag(name = "File Upload", description = "File upload and management operations")
@SecurityRequirement(name = "bearerAuth")
public class FileUploadController {

  @Autowired
  private FileUploadService fileUploadService;

  @Autowired
  private UserFileRepository userFileRepository;

  @PostMapping("/user/{id}")
  public ResponseEntity<UserFilesDto> uploadFile(
    @PathVariable("id") Integer userId,
    @RequestParam("file") MultipartFile file
  ) throws IOException {
    UserFilesDto response = this.fileUploadService.uploadFile(userId, file);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/user/{id}")
  public ResponseEntity<List<UserFilesDto>> getAllByUser(
    @PathVariable("id") Integer userId
  ) {
    List<UserFilesDto> response = this.fileUploadService.getAllByUser(userId);
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/user/{id}")
  public ResponseEntity<Object> deleteByUserId(
    @PathVariable("id") Integer userId
  ) {
    this.fileUploadService.deleteByUserId(userId);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/{fileId}")
  public ResponseEntity<Object> deleteByFileId(
    @PathVariable("fileId") Integer fileId
  ) {
    this.fileUploadService.deleteByFileId(fileId);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/view/{fileId}")
  public ResponseEntity<Resource> viewFile(@PathVariable Integer fileId) {
    UserFiles userFile = userFileRepository.findById(fileId)
        .orElseThrow(() -> new StorageFileNotFoundException("File not found"));
    try {
      Path filePath = Paths.get(userFile.getFilePath());
      Resource resource = new UrlResource(filePath.toUri());
      if (!resource.exists()) {
        throw new StorageFileNotFoundException("File not found");
      }
      String contentType = userFile.getFileType().toLowerCase().contains("png") ? "image/png" : "image/jpeg";
      return ResponseEntity.ok()
          .contentType(MediaType.parseMediaType(contentType))
          .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + userFile.getFileName() + "\"")
          .body(resource);
    } catch (Exception e) {
      throw new StorageFileNotFoundException("Could not read file: " + e.getMessage());
    }
  }

}
