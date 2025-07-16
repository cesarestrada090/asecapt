package com.fitech.app.users.domain.services;

import com.fitech.app.users.application.dto.UserFilesDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface FileUploadService {
  UserFilesDto uploadFile(Integer userId, MultipartFile file) throws IOException;

  List<UserFilesDto> getAllByUser(Integer userId);

  void deleteByUserId(Integer userId);
  void deleteByFileId(Integer fileId);
}
