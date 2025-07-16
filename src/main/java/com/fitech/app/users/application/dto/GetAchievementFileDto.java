package com.fitech.app.users.application.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fitech.app.users.domain.entities.Achievement;
import com.fitech.app.users.domain.entities.UserFiles;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
@Schema(description = "DTO for achievement file information retrieval")
public class GetAchievementFileDto {
  @Schema(description = "Achievement file ID", example = "1")
  private Integer id;
  
  @Schema(description = "User file information")
  private UserFiles userFile;
  
  @Schema(description = "File upload timestamp", accessMode = Schema.AccessMode.READ_ONLY)
  private LocalDateTime uploadedAt;
}
