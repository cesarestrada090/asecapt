package com.fitech.app.users.domain.services.impl;

import com.fitech.app.commons.config.FileProperty;
import com.fitech.app.users.application.exception.StorageException;
import com.fitech.app.users.application.exception.StorageFileNotFoundException;
import com.fitech.app.users.domain.entities.UserFiles;
import com.fitech.app.users.application.dto.UserFilesDto;
import com.fitech.app.users.domain.services.FileUploadService;
import com.fitech.app.users.infrastructure.mappers.UserFilesMapper;
import com.fitech.app.users.infrastructure.repository.UserFileRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class FileUploadServiceImpl implements FileUploadService {

  @Autowired
  private UserFileRepository userFileRepository;

  private final FileProperty fileProperty;

  public FileUploadServiceImpl(FileProperty fileProperty) {
    this.fileProperty = fileProperty;
  }

  @Override
  public UserFilesDto uploadFile(Integer userId, MultipartFile file) throws IOException {
    log.info("Uploading file for user ID: {}  with file name: {}", userId, file.getOriginalFilename());

    String originalName = file.getOriginalFilename();
    String extension = getExtension(originalName);
    String fileName = UUID.randomUUID() + "_" + originalName;

    Path folderPath = Paths.get(this.fileProperty.getUploadDir() + "user_" + userId + "/").toAbsolutePath().normalize();
    log.info("File upload path: {}", folderPath);
    File folder = new File(folderPath.toString());
    if (!folder.exists()) folder.mkdirs();

    if (file.isEmpty()) {
      throw new StorageException("Failed to store empty file.");
    }
    Path destinationFile = folderPath.resolve(fileName).normalize().toAbsolutePath();

    if (!destinationFile.getParent().equals(folderPath)) {
      throw new StorageException(
        "Cannot store file outside current directory.");
    }
    try (InputStream inputStream = file.getInputStream()) {
      Files.copy(inputStream, destinationFile,
        StandardCopyOption.REPLACE_EXISTING);
    }
    log.info("File {} uploaded successfully to: {}", fileName, folderPath);
    UserFiles userFile = new UserFiles(
      0,
      userId,
      fileName,
      extension,
      destinationFile.toString(),
      LocalDateTime.now()
    );

    UserFiles entity = userFileRepository.save(userFile);
    return UserFilesMapper.toDto(entity);
  }

  @Override
  public List<UserFilesDto> getAllByUser(Integer userId) {
    log.info("Listing files for user ID: {}", userId);

    List<UserFiles> userFiles = userFileRepository.findByUserId(userId);

    return userFiles
      .stream()
      .map(UserFilesMapper::toDto)
      .toList();
  }

  @Transactional
  @Override
  public void deleteByUserId(Integer userId) {
    log.info("Removing files for user ID: {}", userId);
    boolean exists = userFileRepository.existsByUserId(userId);
    if (!exists) {
      throw new StorageFileNotFoundException("Files with userId " + userId + ", not found.");
    }

    userFileRepository.deleteByUserId(userId);
  }

  @Transactional
  @Override
  public void deleteByFileId(Integer fileId) {
    log.info("Removing files for File ID: {}", fileId);
    Optional<UserFiles> userFiles = userFileRepository.findById(fileId);
    if (!userFiles.isPresent()) {
      throw new StorageFileNotFoundException("File with id " + fileId + ", not found.");
    }
    userFileRepository.deleteById(fileId);
  }

  private String getExtension(String fileName) {
    return fileName != null && fileName.contains(".")
      ? fileName.substring(fileName.lastIndexOf('.') + 1)
      : "";
  }
}
