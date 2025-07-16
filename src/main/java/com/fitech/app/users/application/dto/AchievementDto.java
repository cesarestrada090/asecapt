package com.fitech.app.users.application.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "DTO for trainer achievements and certifications")
public class AchievementDto {
  @Schema(description = "Achievement ID", example = "1")
  private Long id;
  
  @Schema(description = "Trainer ID who owns the achievement", example = "123")
  private Long trainerId;
  
  @Schema(description = "Type of achievement", example = "CERTIFICATION", allowableValues = {"CERTIFICATION", "AWARD", "COURSE", "DEGREE"})
  private String achievementType;
  
  @Schema(description = "Achievement title", example = "Certificación en Entrenamiento Personal")
  private String title;
  
  @Schema(description = "Achievement description", example = "Certificación internacional en entrenamiento personal y fitness")
  private String description;
  
  @Schema(description = "Date when achievement was obtained", example = "2023-06-15")
  private LocalDate achievedAt;
  
  @Schema(description = "Achievement creation timestamp", accessMode = Schema.AccessMode.READ_ONLY)
  private LocalDateTime createdAt;
  
  @Schema(description = "Last update timestamp", accessMode = Schema.AccessMode.READ_ONLY)
  private LocalDateTime updatedAt;
  
  @Schema(description = "List of files uploaded for this achievement")
  private List<AchievementFileDto> filesUpload;
}
