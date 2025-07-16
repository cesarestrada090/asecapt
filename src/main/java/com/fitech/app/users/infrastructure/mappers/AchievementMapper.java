package com.fitech.app.users.infrastructure.mappers;

import com.fitech.app.commons.util.MapperUtil;
import com.fitech.app.users.domain.entities.Achievement;
import com.fitech.app.users.domain.entities.AchievementFile;
import com.fitech.app.users.application.dto.AchievementDto;
import com.fitech.app.users.application.dto.AchievementFileDto;
import com.fitech.app.users.application.dto.GetAchievementDto;
import com.fitech.app.users.application.dto.GetAchievementFileDto;

public class AchievementMapper {

  public static Achievement toEntity(AchievementDto dto) {
    return MapperUtil.map(dto, Achievement.class);
  }

  public static AchievementDto toDto(Achievement entity) {
    return new AchievementDto(
      entity.getId(),
      entity.getTrainerId(),
      entity.getAchievementType(),
      entity.getTitle(),
      entity.getDescription(),
      entity.getAchievedAt(),
      entity.getCreatedAt(),
      entity.getUpdatedAt(),
      entity.getFiles().stream()
        .map(AchievementMapper::fileDto)
        .toList()
    );
  }

  private static AchievementFileDto fileDto(AchievementFile it) {
    return new AchievementFileDto(it.getId(), it.getUserFile().getId());
  }

  public static GetAchievementDto toGetAchievementDto(Achievement entity) {
    return new GetAchievementDto(
      entity.getId(),
      entity.getTrainerId(),
      entity.getAchievementType(),
      entity.getTitle(),
      entity.getDescription(),
      entity.getAchievedAt(),
      entity.getCreatedAt(),
      entity.getUpdatedAt(),
      entity.getFiles().stream()
        .map(AchievementMapper::getFileDto)
        .toList()
    );
  }

  private static GetAchievementFileDto getFileDto(AchievementFile it) {
    return new GetAchievementFileDto(
      it.getId(),
      it.getUserFile(),
      it.getUploadedAt()
    );
  }
}
