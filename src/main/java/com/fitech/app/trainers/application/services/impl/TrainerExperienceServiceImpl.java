package com.fitech.app.trainers.application.services.impl;

import com.fitech.app.trainers.application.dtos.*;
import com.fitech.app.trainers.application.services.TrainerExperienceService;
import com.fitech.app.trainers.domain.entities.*;
import com.fitech.app.trainers.infrastructure.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TrainerExperienceServiceImpl implements TrainerExperienceService {
    
    private final TrainerEducationRepository educationRepository;
    private final TrainerCertificationRepository certificationRepository;
    private final TrainerRecognitionRepository recognitionRepository;
    
    @Autowired
    public TrainerExperienceServiceImpl(TrainerEducationRepository educationRepository,
                                      TrainerCertificationRepository certificationRepository,
                                      TrainerRecognitionRepository recognitionRepository) {
        this.educationRepository = educationRepository;
        this.certificationRepository = certificationRepository;
        this.recognitionRepository = recognitionRepository;
    }
    
    @Override
    @Transactional(readOnly = true)
    public TrainerExperienceDto getTrainerExperience(Long trainerId) {
        // Load all experience data
        List<TrainerEducationDto> education = getTrainerEducation(trainerId);
        List<TrainerCertificationDto> certifications = getTrainerCertifications(trainerId);
        List<TrainerRecognitionDto> recognitions = getTrainerRecognitions(trainerId);
        TrainerExperienceDto.TrainerExperienceSummaryDto summary = getExperienceSummary(trainerId);
        
        return new TrainerExperienceDto(education, certifications, recognitions, summary);
    }
    
    // Education methods
    @Override
    @Transactional(readOnly = true)
    public List<TrainerEducationDto> getTrainerEducation(Long trainerId) {
        return educationRepository.findByTrainerIdOrderByYearDesc(trainerId)
                .stream()
                .map(TrainerEducationDto::fromEntity)
                .collect(Collectors.toList());
    }
    
    @Override
    public TrainerEducationDto createEducation(Long trainerId, TrainerEducationDto educationDto) {
        // Validate trainer ownership is handled by controller/security
        TrainerEducation education = educationDto.toEntity(trainerId);
        education.setId(null); // Ensure new entity
        TrainerEducation saved = educationRepository.save(education);
        return TrainerEducationDto.fromEntity(saved);
    }
    
    @Override
    public TrainerEducationDto updateEducation(Long trainerId, Long educationId, TrainerEducationDto educationDto) {
        TrainerEducation existing = educationRepository.findByIdAndTrainerId(educationId, trainerId)
                .orElseThrow(() -> new RuntimeException("Estudio no encontrado o no pertenece al entrenador"));
        
        // Update fields
        existing.setTitle(educationDto.getTitle());
        existing.setInstitution(educationDto.getInstitution());
        existing.setYear(educationDto.getYear());
        existing.setType(educationDto.getType());
        existing.setDescription(educationDto.getDescription());
        
        TrainerEducation updated = educationRepository.save(existing);
        return TrainerEducationDto.fromEntity(updated);
    }
    
    @Override
    public void deleteEducation(Long trainerId, Long educationId) {
        TrainerEducation education = educationRepository.findByIdAndTrainerId(educationId, trainerId)
                .orElseThrow(() -> new RuntimeException("Estudio no encontrado o no pertenece al entrenador"));
        educationRepository.delete(education);
    }
    
    // Certification methods
    @Override
    @Transactional(readOnly = true)
    public List<TrainerCertificationDto> getTrainerCertifications(Long trainerId) {
        return certificationRepository.findByTrainerIdOrderByIssueDateDesc(trainerId)
                .stream()
                .map(TrainerCertificationDto::fromEntity)
                .collect(Collectors.toList());
    }
    
    @Override
    public TrainerCertificationDto createCertification(Long trainerId, TrainerCertificationDto certificationDto) {
        TrainerCertification certification = certificationDto.toEntity(trainerId);
        certification.setId(null); // Ensure new entity
        TrainerCertification saved = certificationRepository.save(certification);
        return TrainerCertificationDto.fromEntity(saved);
    }
    
    @Override
    public TrainerCertificationDto updateCertification(Long trainerId, Long certificationId, TrainerCertificationDto certificationDto) {
        TrainerCertification existing = certificationRepository.findByIdAndTrainerId(certificationId, trainerId)
                .orElseThrow(() -> new RuntimeException("Certificación no encontrada o no pertenece al entrenador"));
        
        // Update fields
        existing.setName(certificationDto.getName());
        existing.setOrganization(certificationDto.getOrganization());
        existing.setIssueDate(certificationDto.getIssueDate());
        existing.setExpirationDate(certificationDto.getExpirationDate());
        existing.setCredentialId(certificationDto.getCredentialId());
        existing.setDescription(certificationDto.getDescription());
        
        TrainerCertification updated = certificationRepository.save(existing);
        return TrainerCertificationDto.fromEntity(updated);
    }
    
    @Override
    public void deleteCertification(Long trainerId, Long certificationId) {
        TrainerCertification certification = certificationRepository.findByIdAndTrainerId(certificationId, trainerId)
                .orElseThrow(() -> new RuntimeException("Certificación no encontrada o no pertenece al entrenador"));
        certificationRepository.delete(certification);
    }
    
    // Recognition methods
    @Override
    @Transactional(readOnly = true)
    public List<TrainerRecognitionDto> getTrainerRecognitions(Long trainerId) {
        return recognitionRepository.findByTrainerIdOrderByDateDesc(trainerId)
                .stream()
                .map(TrainerRecognitionDto::fromEntity)
                .collect(Collectors.toList());
    }
    
    @Override
    public TrainerRecognitionDto createRecognition(Long trainerId, TrainerRecognitionDto recognitionDto) {
        TrainerRecognition recognition = recognitionDto.toEntity(trainerId);
        recognition.setId(null); // Ensure new entity
        TrainerRecognition saved = recognitionRepository.save(recognition);
        return TrainerRecognitionDto.fromEntity(saved);
    }
    
    @Override
    public TrainerRecognitionDto updateRecognition(Long trainerId, Long recognitionId, TrainerRecognitionDto recognitionDto) {
        TrainerRecognition existing = recognitionRepository.findByIdAndTrainerId(recognitionId, trainerId)
                .orElseThrow(() -> new RuntimeException("Reconocimiento no encontrado o no pertenece al entrenador"));
        
        // Update fields
        existing.setTitle(recognitionDto.getTitle());
        existing.setOrganization(recognitionDto.getOrganization());
        existing.setDate(recognitionDto.getDate());
        existing.setType(recognitionDto.getType());
        if (recognitionDto.getLevel() != null && !recognitionDto.getLevel().isEmpty()) {
            existing.setLevel(recognitionDto.getLevel());
        } else {
            existing.setLevel(null);
        }
        existing.setDescription(recognitionDto.getDescription());
        
        TrainerRecognition updated = recognitionRepository.save(existing);
        return TrainerRecognitionDto.fromEntity(updated);
    }
    
    @Override
    public void deleteRecognition(Long trainerId, Long recognitionId) {
        TrainerRecognition recognition = recognitionRepository.findByIdAndTrainerId(recognitionId, trainerId)
                .orElseThrow(() -> new RuntimeException("Reconocimiento no encontrado o no pertenece al entrenador"));
        recognitionRepository.delete(recognition);
    }
    
    // Summary and analytics
    @Override
    @Transactional(readOnly = true)
    public TrainerExperienceDto.TrainerExperienceSummaryDto getExperienceSummary(Long trainerId) {
        LocalDate currentDate = LocalDate.now();
        LocalDate twoYearsAgo = currentDate.minusYears(2);
        
        Long totalEducation = educationRepository.countByTrainerId(trainerId);
        Long totalCertifications = certificationRepository.countByTrainerId(trainerId);
        Long totalRecognitions = recognitionRepository.countByTrainerId(trainerId);
        Long validCertifications = certificationRepository.countValidCertificationsByTrainerId(trainerId, currentDate);
        Long expiredCertifications = totalCertifications - validCertifications;
        Long recentRecognitions = recognitionRepository.countRecentRecognitionsByTrainerId(trainerId, twoYearsAgo);
        Long internationalRecognitions = (long) recognitionRepository.findInternationalRecognitionsByTrainerId(trainerId).size();
        
        return new TrainerExperienceDto.TrainerExperienceSummaryDto(
                totalEducation.intValue(),
                totalCertifications.intValue(),
                totalRecognitions.intValue(),
                validCertifications.intValue(),
                expiredCertifications.intValue(),
                recentRecognitions.intValue(),
                internationalRecognitions.intValue()
        );
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<TrainerCertificationDto> getExpiringSoonCertifications(Long trainerId) {
        LocalDate currentDate = LocalDate.now();
        LocalDate futureDate = currentDate.plusDays(30);
        
        return certificationRepository.findCertificationsExpiringSoon(trainerId, currentDate, futureDate)
                .stream()
                .map(TrainerCertificationDto::fromEntity)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<TrainerRecognitionDto> getTopRecognitions(Long trainerId) {
        return recognitionRepository.findTopRecognitionsByTrainerId(trainerId)
                .stream()
                .map(TrainerRecognitionDto::fromEntity)
                .collect(Collectors.toList());
    }
    
    // Validation methods
    @Override
    @Transactional(readOnly = true)
    public boolean hasValidCertifications(Long trainerId) {
        LocalDate currentDate = LocalDate.now();
        Long validCount = certificationRepository.countValidCertificationsByTrainerId(trainerId, currentDate);
        return validCount > 0;
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean hasRecentEducation(Long trainerId) {
        Integer currentYear = LocalDate.now().getYear();
        Integer fromYear = currentYear - 5; // Last 5 years
        List<TrainerEducation> recentEducation = educationRepository.findRecentEducationByTrainerId(trainerId, fromYear);
        return !recentEducation.isEmpty();
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean hasInternationalRecognitions(Long trainerId) {
        List<TrainerRecognition> internationalRecognitions = recognitionRepository.findInternationalRecognitionsByTrainerId(trainerId);
        return !internationalRecognitions.isEmpty();
    }
} 