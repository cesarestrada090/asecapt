package com.fitech.app.trainers.domain.services;

import com.fitech.app.trainers.application.dto.CreateTrainerServiceDto;
import com.fitech.app.trainers.application.dto.TrainerServiceDto;

import java.util.List;

public interface TrainerServiceService {
    
    TrainerServiceDto createService(Integer trainerId, CreateTrainerServiceDto createDto);
    
    TrainerServiceDto updateService(Integer serviceId, Integer trainerId, CreateTrainerServiceDto updateDto);
    
    TrainerServiceDto getServiceById(Integer serviceId);
    
    List<TrainerServiceDto> getServicesByTrainer(Integer trainerId);
    
    List<TrainerServiceDto> getActiveServicesByTrainer(Integer trainerId);
    
    List<TrainerServiceDto> getAllActiveServices();
    
    List<TrainerServiceDto> getServicesByType(Boolean isInPerson);
    
    List<TrainerServiceDto> getServicesByDistrict(String district);
    
    void deleteService(Integer serviceId, Integer trainerId);
    
    void deactivateService(Integer serviceId, Integer trainerId);
    
    void activateService(Integer serviceId, Integer trainerId);
    
    Long countActiveServicesByTrainer(Integer trainerId);
} 