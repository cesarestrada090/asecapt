package com.fitech.app.trainers.domain.services.impl;

import com.fitech.app.trainers.domain.entities.ServiceType;
import com.fitech.app.trainers.domain.services.ServiceTypeService;
import com.fitech.app.trainers.application.dto.CreateTrainerServiceDto;
import com.fitech.app.trainers.application.dto.TrainerServiceDto;
import com.fitech.app.trainers.domain.entities.ServiceDistrict;
import com.fitech.app.trainers.domain.entities.TrainerService;
import com.fitech.app.trainers.domain.services.TrainerServiceService;
import com.fitech.app.trainers.infrastructure.repository.TrainerServiceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class TrainerServiceServiceImpl implements TrainerServiceService {

    @Autowired
    private TrainerServiceRepository trainerServiceRepository;
    
    @Autowired
    private ServiceTypeService serviceTypeService;

    @Override
    public TrainerServiceDto createService(Integer trainerId, CreateTrainerServiceDto createDto) {
        log.info("Creating new service for trainer ID: {}", trainerId);
        
        // Verificar si ya existe un servicio del mismo tipo para este trainer
        Optional<TrainerService> existingService = trainerServiceRepository
            .findByTrainerIdAndServiceTypeId(trainerId, createDto.getServiceTypeId());
        
        if (existingService.isPresent()) {
            throw new RuntimeException("Ya existe un servicio de este tipo para el trainer. " +
                "Solo puede tener un servicio por tipo.");
        }
        
        // Obtener el ServiceType
        ServiceType serviceType = serviceTypeService.getServiceTypeEntityById(createDto.getServiceTypeId())
            .orElseThrow(() -> new RuntimeException("Service type not found with ID: " + createDto.getServiceTypeId()));
        
        TrainerService service = new TrainerService();
        service.setTrainerId(trainerId);
        service.setServiceType(serviceType);
        service.setDescription(createDto.getDescription());
        
        service.setTotalPrice(createDto.getTotalPrice());
        service.setIsInPerson(createDto.getIsInPerson());
        service.setTransportIncluded(createDto.getTransportIncluded());
        service.setTransportCostPerSession(createDto.getTransportCostPerSession());
        service.setCountry(createDto.getCountry());
        
        if (createDto.getIsInPerson() && createDto.getDistricts() != null) {
            for (String districtName : createDto.getDistricts()) {
                ServiceDistrict district = new ServiceDistrict(districtName);
                service.addDistrict(district);
            }
        }

        TrainerService savedService = trainerServiceRepository.save(service);
        log.info("Service created with ID: {}", savedService.getId());
        
        return TrainerServiceDto.fromEntity(savedService);
    }

    @Override
    public TrainerServiceDto updateService(Integer serviceId, Integer trainerId, CreateTrainerServiceDto updateDto) {
        log.info("Updating service ID: {} for trainer ID: {}", serviceId, trainerId);
        
        TrainerService service = trainerServiceRepository.findById(serviceId)
            .orElseThrow(() -> new RuntimeException("Service not found with ID: " + serviceId));
            
        if (!service.getTrainerId().equals(trainerId)) {
            throw new RuntimeException("Service does not belong to trainer");
        }

        // Verify if the service already exists
        if (!service.getServiceType().getId().equals(updateDto.getServiceTypeId())) {
            Optional<TrainerService> existingService = trainerServiceRepository
                .findByTrainerIdAndServiceTypeId(trainerId, updateDto.getServiceTypeId());
            
            if (existingService.isPresent()) {
                throw new RuntimeException("Ya existe un servicio de este tipo para el trainer. " +
                    "Solo puede tener un servicio por tipo.");
            }
        }

        // Get ServiceType
        ServiceType serviceType = serviceTypeService.getServiceTypeEntityById(updateDto.getServiceTypeId())
            .orElseThrow(() -> new RuntimeException("Service type not found with ID: " + updateDto.getServiceTypeId()));

        service.setServiceType(serviceType);
        service.setDescription(updateDto.getDescription());
        service.setTotalPrice(updateDto.getTotalPrice());
        service.setIsInPerson(updateDto.getIsInPerson());
        service.setTransportIncluded(updateDto.getTransportIncluded());
        service.setTransportCostPerSession(updateDto.getTransportCostPerSession());
        service.setCountry(updateDto.getCountry());

        // Actualizar distritos
        service.getDistricts().clear();
        if (updateDto.getIsInPerson() && updateDto.getDistricts() != null) {
            for (String districtName : updateDto.getDistricts()) {
                ServiceDistrict district = new ServiceDistrict(districtName);
                service.addDistrict(district);
            }
        }

        TrainerService savedService = trainerServiceRepository.save(service);
        return TrainerServiceDto.fromEntity(savedService);
    }

    @Override
    @Transactional(readOnly = true)
    public TrainerServiceDto getServiceById(Integer serviceId) {
        TrainerService service = trainerServiceRepository.findById(serviceId)
            .orElseThrow(() -> new RuntimeException("Service not found with ID: " + serviceId));
        return TrainerServiceDto.fromEntity(service);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrainerServiceDto> getServicesByTrainer(Integer trainerId) {
        List<TrainerService> services = trainerServiceRepository.findByTrainerId(trainerId);
        return services.stream()
            .map(TrainerServiceDto::fromEntity)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrainerServiceDto> getActiveServicesByTrainer(Integer trainerId) {
        List<TrainerService> services = trainerServiceRepository.findByTrainerIdAndIsActive(trainerId, true);
        return services.stream()
            .map(TrainerServiceDto::fromEntity)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrainerServiceDto> getAllActiveServices() {
        List<TrainerService> services = trainerServiceRepository.findByIsActive(true);
        return services.stream()
            .map(TrainerServiceDto::fromEntity)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrainerServiceDto> getServicesByType(Boolean isInPerson) {
        List<TrainerService> services = trainerServiceRepository.findActiveServicesByType(isInPerson);
        return services.stream()
            .map(TrainerServiceDto::fromEntity)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrainerServiceDto> getServicesByDistrict(String district) {
        List<TrainerService> services = trainerServiceRepository.findActiveServicesByDistrict(district);
        return services.stream()
            .map(TrainerServiceDto::fromEntity)
            .collect(Collectors.toList());
    }

    @Override
    public void deleteService(Integer serviceId, Integer trainerId) {
        TrainerService service = trainerServiceRepository.findById(serviceId)
            .orElseThrow(() -> new RuntimeException("Service not found with ID: " + serviceId));
            
        if (!service.getTrainerId().equals(trainerId)) {
            throw new RuntimeException("Service does not belong to trainer");
        }

        trainerServiceRepository.delete(service);
        log.info("Service deleted: {}", serviceId);
    }

    @Override
    public void deactivateService(Integer serviceId, Integer trainerId) {
        TrainerService service = trainerServiceRepository.findById(serviceId)
            .orElseThrow(() -> new RuntimeException("Service not found with ID: " + serviceId));
            
        if (!service.getTrainerId().equals(trainerId)) {
            throw new RuntimeException("Service does not belong to trainer");
        }

        service.setIsActive(false);
        trainerServiceRepository.save(service);
        log.info("Service deactivated: {}", serviceId);
    }

    @Override
    public void activateService(Integer serviceId, Integer trainerId) {
        TrainerService service = trainerServiceRepository.findById(serviceId)
            .orElseThrow(() -> new RuntimeException("Service not found with ID: " + serviceId));
            
        if (!service.getTrainerId().equals(trainerId)) {
            throw new RuntimeException("Service does not belong to trainer");
        }

        service.setIsActive(true);
        trainerServiceRepository.save(service);
        log.info("Service activated: {}", serviceId);
    }

    @Override
    @Transactional(readOnly = true)
    public Long countActiveServicesByTrainer(Integer trainerId) {
        return trainerServiceRepository.countActiveServicesByTrainer(trainerId);
    }
} 