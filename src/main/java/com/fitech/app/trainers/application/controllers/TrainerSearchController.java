package com.fitech.app.trainers.application.controllers;

import com.fitech.app.users.application.dto.TrainerSearchDto;
import com.fitech.app.users.application.dto.UserResponseDto;
import com.fitech.app.users.application.dto.PublicTrainerDto;
import com.fitech.app.users.domain.services.TrainerSearchService;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/v1/app/trainers")
@Tag(name = "Trainer Search", description = "Search and discovery operations for trainers")
@SecurityRequirement(name = "bearerAuth")
public class TrainerSearchController {

    @Autowired
    private TrainerSearchService trainerSearchService;

    @PostMapping("/search")
    public ResponseEntity<List<PublicTrainerDto>> searchTrainers(@RequestBody TrainerSearchDto searchDto) {
        log.info("Search trainers request: {}", searchDto);
        List<PublicTrainerDto> trainers = trainerSearchService.searchTrainersPublic(searchDto);
        return ResponseEntity.ok(trainers);
    }

    @GetMapping("/all")
    public ResponseEntity<List<PublicTrainerDto>> getAllTrainers() {
        log.info("Get all trainers request");
        List<PublicTrainerDto> trainers = trainerSearchService.getAllTrainersPublic();
        return ResponseEntity.ok(trainers);
    }

    @GetMapping("/{trainerId}")
    public ResponseEntity<UserResponseDto> getTrainerProfile(@PathVariable Integer trainerId) {
        log.info("Get trainer profile request for ID: {}", trainerId);
        UserResponseDto trainer = trainerSearchService.getTrainerProfile(trainerId);
        return ResponseEntity.ok(trainer);
    }
} 