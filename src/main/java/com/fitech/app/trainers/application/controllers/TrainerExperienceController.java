package com.fitech.app.trainers.application.controllers;

import com.fitech.app.trainers.application.dtos.*;
import com.fitech.app.trainers.application.services.TrainerExperienceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/app/trainers/{trainerId}/experience")
public class TrainerExperienceController {
    
    private final TrainerExperienceService experienceService;
    
    @Autowired
    public TrainerExperienceController(TrainerExperienceService experienceService) {
        this.experienceService = experienceService;
    }
    
    // Get complete experience data
    @GetMapping
    public ResponseEntity<TrainerExperienceDto> getTrainerExperience(@PathVariable Long trainerId) {
        try {
            TrainerExperienceDto experience = experienceService.getTrainerExperience(trainerId);
            return ResponseEntity.ok(experience);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }
    
    // Education endpoints
    @GetMapping("/education")
    public ResponseEntity<List<TrainerEducationDto>> getEducation(@PathVariable Long trainerId) {
        try {
            List<TrainerEducationDto> education = experienceService.getTrainerEducation(trainerId);
            return ResponseEntity.ok(education);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }
    
    @PostMapping("/education")
    public ResponseEntity<TrainerEducationDto> createEducation(@PathVariable Long trainerId, 
                                                             @RequestBody TrainerEducationDto educationDto) {
        try {
            TrainerEducationDto created = experienceService.createEducation(trainerId, educationDto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(created);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(null);
        }
    }
    
    @PutMapping("/education/{educationId}")
    public ResponseEntity<TrainerEducationDto> updateEducation(@PathVariable Long trainerId,
                                                             @PathVariable Long educationId,
                                                             @RequestBody TrainerEducationDto educationDto) {
        try {
            TrainerEducationDto updated = experienceService.updateEducation(trainerId, educationId, educationDto);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }
    
    @DeleteMapping("/education/{educationId}")
    public ResponseEntity<Void> deleteEducation(@PathVariable Long trainerId,
                                              @PathVariable Long educationId) {
        try {
            experienceService.deleteEducation(trainerId, educationId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Certification endpoints
    @GetMapping("/certifications")
    public ResponseEntity<List<TrainerCertificationDto>> getCertifications(@PathVariable Long trainerId) {
        try {
            List<TrainerCertificationDto> certifications = experienceService.getTrainerCertifications(trainerId);
            return ResponseEntity.ok(certifications);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }
    
    @PostMapping("/certifications")
    public ResponseEntity<TrainerCertificationDto> createCertification(@PathVariable Long trainerId,
                                                                     @RequestBody TrainerCertificationDto certificationDto) {
        try {
            TrainerCertificationDto created = experienceService.createCertification(trainerId, certificationDto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(created);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(null);
        }
    }
    
    @PutMapping("/certifications/{certificationId}")
    public ResponseEntity<TrainerCertificationDto> updateCertification(@PathVariable Long trainerId,
                                                                     @PathVariable Long certificationId,
                                                                     @RequestBody TrainerCertificationDto certificationDto) {
        try {
            TrainerCertificationDto updated = experienceService.updateCertification(trainerId, certificationId, certificationDto);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }
    
    @DeleteMapping("/certifications/{certificationId}")
    public ResponseEntity<Void> deleteCertification(@PathVariable Long trainerId,
                                                  @PathVariable Long certificationId) {
        try {
            experienceService.deleteCertification(trainerId, certificationId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Recognition endpoints
    @GetMapping("/recognitions")
    public ResponseEntity<List<TrainerRecognitionDto>> getRecognitions(@PathVariable Long trainerId) {
        try {
            List<TrainerRecognitionDto> recognitions = experienceService.getTrainerRecognitions(trainerId);
            return ResponseEntity.ok(recognitions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }
    
    @PostMapping("/recognitions")
    public ResponseEntity<TrainerRecognitionDto> createRecognition(@PathVariable Long trainerId,
                                                                 @RequestBody TrainerRecognitionDto recognitionDto) {
        try {
            TrainerRecognitionDto created = experienceService.createRecognition(trainerId, recognitionDto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(created);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(null);
        }
    }
    
    @PutMapping("/recognitions/{recognitionId}")
    public ResponseEntity<TrainerRecognitionDto> updateRecognition(@PathVariable Long trainerId,
                                                                 @PathVariable Long recognitionId,
                                                                 @RequestBody TrainerRecognitionDto recognitionDto) {
        try {
            TrainerRecognitionDto updated = experienceService.updateRecognition(trainerId, recognitionId, recognitionDto);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }
    
    @DeleteMapping("/recognitions/{recognitionId}")
    public ResponseEntity<Void> deleteRecognition(@PathVariable Long trainerId,
                                                @PathVariable Long recognitionId) {
        try {
            experienceService.deleteRecognition(trainerId, recognitionId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Analytics and summary endpoints
    @GetMapping("/summary")
    public ResponseEntity<TrainerExperienceDto.TrainerExperienceSummaryDto> getSummary(@PathVariable Long trainerId) {
        try {
            TrainerExperienceDto.TrainerExperienceSummaryDto summary = experienceService.getExperienceSummary(trainerId);
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }
    
    @GetMapping("/certifications/expiring-soon")
    public ResponseEntity<List<TrainerCertificationDto>> getExpiringSoonCertifications(@PathVariable Long trainerId) {
        try {
            List<TrainerCertificationDto> expiring = experienceService.getExpiringSoonCertifications(trainerId);
            return ResponseEntity.ok(expiring);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }
    
    @GetMapping("/recognitions/top")
    public ResponseEntity<List<TrainerRecognitionDto>> getTopRecognitions(@PathVariable Long trainerId) {
        try {
            List<TrainerRecognitionDto> topRecognitions = experienceService.getTopRecognitions(trainerId);
            return ResponseEntity.ok(topRecognitions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }
} 