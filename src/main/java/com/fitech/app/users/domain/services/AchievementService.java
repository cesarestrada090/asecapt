package com.fitech.app.users.domain.services;

import com.fitech.app.users.application.dto.AchievementDto;
import com.fitech.app.users.application.dto.GetAchievementDto;

import java.util.List;

public interface AchievementService {
  List<GetAchievementDto> getAchievementsByTrainerId(Long trainerId);

  AchievementDto createAchievement(Long trainerId, AchievementDto achievement);

  AchievementDto updateAchievement(Long achievementId, AchievementDto updated);

  void deleteAchievement(Long achievementId);
}
