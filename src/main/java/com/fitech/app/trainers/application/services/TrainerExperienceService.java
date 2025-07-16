package com.fitech.app.trainers.application.services;

import com.fitech.app.trainers.application.dtos.*;
import java.util.List;

public interface TrainerExperienceService {
    
    // Get complete experience data
    TrainerExperienceDto getTrainerExperience(Long trainerId);
    
    // Education methods
    List<TrainerEducationDto> getTrainerEducation(Long trainerId);
    TrainerEducationDto createEducation(Long trainerId, TrainerEducationDto educationDto);
    TrainerEducationDto updateEducation(Long trainerId, Long educationId, TrainerEducationDto educationDto);
    void deleteEducation(Long trainerId, Long educationId);
    
    // Certification methods
    List<TrainerCertificationDto> getTrainerCertifications(Long trainerId);
    TrainerCertificationDto createCertification(Long trainerId, TrainerCertificationDto certificationDto);
    TrainerCertificationDto updateCertification(Long trainerId, Long certificationId, TrainerCertificationDto certificationDto);
    void deleteCertification(Long trainerId, Long certificationId);
    
    // Recognition methods
    List<TrainerRecognitionDto> getTrainerRecognitions(Long trainerId);
    TrainerRecognitionDto createRecognition(Long trainerId, TrainerRecognitionDto recognitionDto);
    TrainerRecognitionDto updateRecognition(Long trainerId, Long recognitionId, TrainerRecognitionDto recognitionDto);
    void deleteRecognition(Long trainerId, Long recognitionId);
    
    // Summary and analytics
    TrainerExperienceDto.TrainerExperienceSummaryDto getExperienceSummary(Long trainerId);
    List<TrainerCertificationDto> getExpiringSoonCertifications(Long trainerId);
    List<TrainerRecognitionDto> getTopRecognitions(Long trainerId);
    
    // Validation methods
    boolean hasValidCertifications(Long trainerId);
    boolean hasRecentEducation(Long trainerId);
    boolean hasInternationalRecognitions(Long trainerId);
} 