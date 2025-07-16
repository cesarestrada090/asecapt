package com.fitech.app.users.application.controllers;

import com.fitech.app.users.application.dto.AchievementDto;
import com.fitech.app.users.application.dto.GetAchievementDto;
import com.fitech.app.users.domain.services.AchievementService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/v1/app/achievements")
@Tag(name = "Achievements", description = "Trainer achievements and certifications management")
@SecurityRequirement(name = "bearerAuth")
public class AchievementController {

  @Autowired
  private AchievementService achievementService;

  @GetMapping("/trainers/{trainerId}")
  public ResponseEntity<List<GetAchievementDto>> listAchievements(@PathVariable Long trainerId) {
    return ResponseEntity.ok(achievementService.getAchievementsByTrainerId(trainerId));
  }

  @PostMapping("/trainers/{trainerId}")
  public ResponseEntity<AchievementDto> createAchievement(
    @PathVariable Long trainerId,
    @RequestBody AchievementDto achievement) {
    AchievementDto created = achievementService.createAchievement(trainerId, achievement);
    return new ResponseEntity<>(created, HttpStatus.CREATED);
  }

  @PutMapping("/{achievementId}")
  public ResponseEntity<AchievementDto> updateAchievement(
    @PathVariable Long achievementId,
    @RequestBody AchievementDto achievement) {
    return ResponseEntity.ok(achievementService.updateAchievement(achievementId, achievement));
  }

  @DeleteMapping("/{achievementId}")
  public ResponseEntity<Void> deleteAchievement(@PathVariable Long achievementId) {
    achievementService.deleteAchievement(achievementId);
    return ResponseEntity.noContent().build();
  }

}
