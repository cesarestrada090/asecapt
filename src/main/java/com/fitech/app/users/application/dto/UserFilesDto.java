package com.fitech.app.users.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO for file upload and management information")
public class UserFilesDto implements Serializable {
  @Schema(description = "File ID", example = "1")
  private Integer id;
  
  @Schema(description = "User ID who uploaded the file", example = "123")
  private Integer userId;
  
  @Schema(description = "Original file name", example = "certificado.pdf")
  private String fileName;
  
  @Schema(description = "File MIME type", example = "application/pdf", allowableValues = {"image/jpeg", "image/png", "application/pdf", "video/mp4"})
  private String fileType;
  
  @JsonProperty("file_path")
  @Schema(description = "File storage path", example = "/uploads/users/123/certificado.pdf")
  private String filePath;
  
  @Schema(description = "File upload timestamp", accessMode = Schema.AccessMode.READ_ONLY)
  private LocalDateTime uploadedAt;
}
