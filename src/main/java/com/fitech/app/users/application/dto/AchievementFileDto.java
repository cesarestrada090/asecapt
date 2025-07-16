package com.fitech.app.users.application.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
@Schema(description = "DTO for achievement file upload information")
public class AchievementFileDto {
  @Schema(description = "Achievement file ID", example = "1")
  private Integer id;
  
  @Schema(description = "User file ID reference", example = "456")
  private Integer userFileId;
}
