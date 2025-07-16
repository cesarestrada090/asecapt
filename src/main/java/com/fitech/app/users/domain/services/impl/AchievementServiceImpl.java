package com.fitech.app.users.domain.services.impl;

import com.fitech.app.users.application.exception.EntityNotFoundException;
import com.fitech.app.users.domain.entities.Achievement;
import com.fitech.app.users.domain.entities.AchievementFile;
import com.fitech.app.users.application.dto.AchievementDto;
import com.fitech.app.users.application.dto.AchievementFileDto;
import com.fitech.app.users.application.dto.GetAchievementDto;
import com.fitech.app.users.domain.services.AchievementService;
import com.fitech.app.users.infrastructure.mappers.AchievementMapper;
import com.fitech.app.users.infrastructure.repository.AchievementRepository;
import com.fitech.app.users.infrastructure.repository.UserFileRepository;
import com.fitech.app.users.infrastructure.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class AchievementServiceImpl implements AchievementService {

  @Autowired
  private AchievementRepository achievementRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private UserFileRepository userFileRepository;

  @Override
  public List<GetAchievementDto> getAchievementsByTrainerId(Long trainerId) {

    List<Achievement> achievementsEntity = achievementRepository.findByTrainerId(trainerId);
    log.info("Achievements found with trainer id {}: {}", trainerId, achievementsEntity);
    return achievementsEntity.stream().map(AchievementMapper::toGetAchievementDto).toList();
  }

  @Override
  @Transactional
  public AchievementDto createAchievement(Long trainerId, AchievementDto dto) {
    Achievement achievement = AchievementMapper.toEntity(dto);
    achievement.setTrainerId(trainerId);

    log.info("Achievement created with trainer id {} and AchievementDto {}", trainerId, achievement);

    if (dto.getFilesUpload() != null) {
      for (AchievementFileDto fileDto : dto.getFilesUpload()) {
        log.info("Adding file to achievement: {}", fileDto.toString());
        AchievementFile file = new AchievementFile();
        file.setUserFile(userFileRepository.findById(fileDto.getUserFileId()).orElseThrow());
        achievement.addFile(file);
      }
    }
    log.info("Achievement after adding files: {}", achievement);
    return AchievementMapper.toDto(achievementRepository.save(achievement));
  }

  @Override
  @Transactional
  public AchievementDto updateAchievement(Long achievementId, AchievementDto updated) {
    log.info("Achievement update with achievementId id {} and AchievementDto {}", achievementId, updated);

    Integer userId = Integer.parseInt(updated.getTrainerId().toString());
    log.info("trainerId {}", userId);
    if (!userRepository.existsById(userId)) {
      throw new EntityNotFoundException("trainer not found with ID: " + userId);
    }

    Achievement achievementEntity = achievementRepository.findById(achievementId)
        .orElseThrow(() -> new EntityNotFoundException("Achievement not found with ID: " + achievementId));

    achievementEntity.getFiles().clear();

    if (updated.getFilesUpload() != null) {
      for (AchievementFileDto fileDto : updated.getFilesUpload()) {
        AchievementFile file = new AchievementFile();
        file.setUserFile(userFileRepository.findById(fileDto.getUserFileId()).orElseThrow());
        achievementEntity.addFile(file);
      }
    }

    return AchievementMapper.toDto(achievementRepository.save(achievementEntity));
  }

  @Override
  public void deleteAchievement(Long achievementId) {
    if (!achievementRepository.existsById(achievementId)) {
      throw new EntityNotFoundException("Achievement not found with ID: " + achievementId);
    }
    achievementRepository.deleteById(achievementId);
  }
}
