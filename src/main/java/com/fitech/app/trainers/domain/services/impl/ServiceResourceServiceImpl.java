package com.fitech.app.trainers.domain.services.impl;

import com.fitech.app.trainers.application.dto.ClientResourceGroupDto;
import com.fitech.app.trainers.application.dto.ServiceResourceDto;
import com.fitech.app.trainers.domain.entities.ServiceResource;
import com.fitech.app.trainers.domain.services.ServiceResourceService;
import com.fitech.app.trainers.infrastructure.repositories.ServiceResourceRepository;
import com.fitech.app.trainers.infrastructure.repository.TrainerServiceRepository;
import com.fitech.app.users.infrastructure.repository.UserRepository;
import com.fitech.app.users.domain.entities.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ServiceResourceServiceImpl implements ServiceResourceService {

    private final ServiceResourceRepository serviceResourceRepository;
    private final TrainerServiceRepository trainerServiceRepository;
    private final UserRepository userRepository;

    @Override
    public ServiceResourceDto createResource(ServiceResourceDto resourceDto) {
        log.info("Creating new resource: {} for service: {} and client: {}", 
                resourceDto.getResourceName(), resourceDto.getServiceId(), resourceDto.getClientId());

        // Verificar que al menos uno de serviceId o clientId está presente
        if (resourceDto.getServiceId() == null && resourceDto.getClientId() == null) {
            throw new RuntimeException("Either serviceId or clientId must be provided");
        }

        // Si hay serviceId, verificar que el servicio existe
        if (resourceDto.getServiceId() != null && !trainerServiceRepository.existsById(resourceDto.getServiceId())) {
            throw new RuntimeException("Service not found with ID: " + resourceDto.getServiceId());
        }

        // Si existe un recurso con el mismo nombre, agregar un sufijo numérico
        String baseName = resourceDto.getResourceName();
        int counter = 1;
        String newName = baseName;
        
        // Check unique name based on context (service and/or client)
        boolean nameExists;
        if (resourceDto.getServiceId() != null && resourceDto.getClientId() != null) {
            // Check uniqueness by service AND client for maximum precision
            nameExists = serviceResourceRepository.existsByServiceIdAndClientIdAndResourceNameAndIsActiveTrue(
                    resourceDto.getServiceId(), resourceDto.getClientId(), newName);
        } else if (resourceDto.getServiceId() != null) {
            nameExists = serviceResourceRepository.existsByServiceIdAndResourceNameAndIsActiveTrue(
                    resourceDto.getServiceId(), newName);
        } else {
            nameExists = serviceResourceRepository.existsByClientIdAndResourceNameAndIsActiveTrue(
                    resourceDto.getClientId(), newName);
        }
        
        while (nameExists) {
            counter++;
            newName = baseName + " (" + counter + ")";
            
            if (resourceDto.getServiceId() != null && resourceDto.getClientId() != null) {
                nameExists = serviceResourceRepository.existsByServiceIdAndClientIdAndResourceNameAndIsActiveTrue(
                        resourceDto.getServiceId(), resourceDto.getClientId(), newName);
            } else if (resourceDto.getServiceId() != null) {
                nameExists = serviceResourceRepository.existsByServiceIdAndResourceNameAndIsActiveTrue(
                        resourceDto.getServiceId(), newName);
            } else {
                nameExists = serviceResourceRepository.existsByClientIdAndResourceNameAndIsActiveTrue(
                        resourceDto.getClientId(), newName);
            }
        }
        
        resourceDto.setResourceName(newName);
        ServiceResource resource = resourceDto.toEntity();
        resource.setIsActive(true);
        
        ServiceResource savedResource = serviceResourceRepository.save(resource);
        log.info("Resource created successfully with ID: {}", savedResource.getId());
        
        return new ServiceResourceDto(savedResource);
    }

    @Override
    public ServiceResourceDto updateResource(Integer resourceId, ServiceResourceDto resourceDto) {
        log.info("Updating resource with ID: {}", resourceId);

        ServiceResource existingResource = serviceResourceRepository.findById(resourceId)
                .orElseThrow(() -> new RuntimeException("Resource not found with ID: " + resourceId));

        // Verificar nombre único solo si cambió
        if (!existingResource.getResourceName().equals(resourceDto.getResourceName())) {
            if (serviceResourceRepository.existsByServiceIdAndResourceNameAndIsActiveTrue(
                    resourceDto.getServiceId(), resourceDto.getResourceName())) {
                throw new RuntimeException("Resource with name '" + resourceDto.getResourceName() + "' already exists for this service");
            }
        }

        // Actualizar campos
        existingResource.setResourceName(resourceDto.getResourceName());
        existingResource.setResourceType(resourceDto.getResourceType());
        existingResource.setResourceObjective(resourceDto.getResourceObjective());
        existingResource.setResourceDetails(resourceDto.getResourceDetails());
        existingResource.setStartDate(resourceDto.getStartDate());
        existingResource.setEndDate(resourceDto.getEndDate());
        existingResource.setTrainerNotes(resourceDto.getTrainerNotes());

        ServiceResource updatedResource = serviceResourceRepository.save(existingResource);
        log.info("Resource updated successfully: {}", updatedResource.getId());

        return new ServiceResourceDto(updatedResource);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ServiceResourceDto> getResourceById(Integer resourceId) {
        return serviceResourceRepository.findById(resourceId)
                .filter(ServiceResource::getIsActive)
                .map(ServiceResourceDto::new);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ServiceResourceDto> getResourcesByServiceId(Integer serviceId) {
        return serviceResourceRepository.findByServiceIdAndIsActiveTrue(serviceId)
                .stream()
                .map(ServiceResourceDto::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ServiceResourceDto> getResourcesByServiceIdAndType(Integer serviceId, ServiceResource.ResourceType resourceType) {
        return serviceResourceRepository.findByServiceIdAndResourceTypeAndIsActiveTrue(serviceId, resourceType)
                .stream()
                .map(ServiceResourceDto::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ServiceResourceDto> getResourcesByTrainerId(Integer trainerId) {
        // Ahora obtenemos TODOS los recursos del trainer (con servicio y directos)
        return serviceResourceRepository.findAllResourcesByTrainerIdAndIsActiveTrue(trainerId)
                .stream()
                .map(ServiceResourceDto::new)
                .collect(Collectors.toList());
    }

    @Override
    public void deactivateResource(Integer resourceId) {
        log.info("Deactivating resource with ID: {}", resourceId);
        
        ServiceResource resource = serviceResourceRepository.findById(resourceId)
                .orElseThrow(() -> new RuntimeException("Resource not found with ID: " + resourceId));
        
        resource.setIsActive(false);
        serviceResourceRepository.save(resource);
        
        log.info("Resource deactivated successfully: {}", resourceId);
    }

    @Override
    public void activateResource(Integer resourceId) {
        log.info("Activating resource with ID: {}", resourceId);
        
        ServiceResource resource = serviceResourceRepository.findById(resourceId)
                .orElseThrow(() -> new RuntimeException("Resource not found with ID: " + resourceId));
        
        resource.setIsActive(true);
        serviceResourceRepository.save(resource);
        
        log.info("Resource activated successfully: {}", resourceId);
    }

    @Override
    public void deleteResource(Integer resourceId) {
        log.info("Permanently deleting resource with ID: {}", resourceId);
        
        if (!serviceResourceRepository.existsById(resourceId)) {
            throw new RuntimeException("Resource not found with ID: " + resourceId);
        }
        
        serviceResourceRepository.deleteById(resourceId);
        log.info("Resource deleted permanently: {}", resourceId);
    }

    @Override
    @Transactional(readOnly = true)
    public Long countActiveResourcesByServiceId(Integer serviceId) {
        return serviceResourceRepository.countByServiceIdAndIsActiveTrue(serviceId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isResourceOwnedByTrainer(Integer resourceId, Integer trainerId) {
        return serviceResourceRepository.findById(resourceId)
                .map(resource -> {
                    // Verificar si el recurso pertenece al trainer directamente o a través de un servicio
                    if (resource.getTrainerId() != null && resource.getTrainerId().equals(trainerId)) {
                        return true; // Recurso directo del trainer
                    }
                    if (resource.getService() != null && resource.getService().getTrainerId().equals(trainerId)) {
                        return true; // Recurso a través de servicio del trainer
                    }
                    return false;
                })
                .orElse(false);
    }

    @Override
    @Transactional(readOnly = true)
    public List<com.fitech.app.trainers.application.dto.ClientResourceResponseDto> getResourcesForClient(Integer clientId, ServiceResource.ResourceType resourceType) {
        log.info("Getting resources of type {} for client: {}", resourceType, clientId);
        
        List<com.fitech.app.trainers.application.dto.ClientResourceResponseDto> result = new ArrayList<>();
        
        List<ServiceResource> directResources = serviceResourceRepository.findByClientIdAndResourceTypeAndServiceIdIsNullAndIsActiveTrue(clientId, resourceType);
        log.info("Found {} direct resources for client {}", directResources.size(), clientId);
        for (ServiceResource resource : directResources) {
            log.info("Direct resource: ID={}, Name={}, ClientId={}", resource.getId(), resource.getResourceName(), resource.getClientId());
        }
        result.addAll(directResources.stream()
                .map(this::mapToDirectClientResourceResponse)
                .collect(Collectors.toList()));
        
        List<Object[]> serviceResourcesWithClientInfo = serviceResourceRepository.findActiveResourcesByClientAndTypeWithClientInfo(clientId, resourceType);
        for (Object[] result_obj : serviceResourcesWithClientInfo) {
            ServiceResource resource = (ServiceResource) result_obj[0];
            Integer contractClientId = (Integer) result_obj[1];
            String firstName = (String) result_obj[2];
            String lastName = (String) result_obj[3];
        }
        result.addAll(serviceResourcesWithClientInfo.stream()
                .map(this::mapToClientResourceResponseWithClientInfo)
                .toList());
        
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<com.fitech.app.trainers.application.dto.ClientResourceResponseDto> getAllResourcesForClient(Integer clientId) {
        log.info("Getting all resources for client: {}", clientId);
        
        List<com.fitech.app.trainers.application.dto.ClientResourceResponseDto> result = new ArrayList<>();
        
        List<ServiceResource> directResources = serviceResourceRepository.findByClientIdAndServiceIdIsNullAndIsActiveTrue(clientId);
        result.addAll(directResources.stream()
                .map(this::mapToDirectClientResourceResponse)
                .collect(Collectors.toList()));
        
        List<Object[]> serviceResourcesWithClientInfo = serviceResourceRepository.findActiveResourcesByClientWithClientInfo(clientId);
        result.addAll(serviceResourcesWithClientInfo.stream()
                .map(this::mapToClientResourceResponseWithClientInfo)
                .collect(Collectors.toList()));
        
        log.info("Found {} total resources for client {}: {} direct, {} from services", 
                result.size(), clientId, directResources.size(), serviceResourcesWithClientInfo.size());
        
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<com.fitech.app.trainers.application.dto.ClientResourceResponseDto> getAllResourcesForTrainerClients(Integer trainerId) {
        log.info("Getting all resources for trainer's clients: {}", trainerId);
        
        return serviceResourceRepository.findAllActiveResourcesByTrainerWithClientInfo(trainerId)
                .stream()
                .map(this::mapToUnifiedClientResourceResponseWithClientInfo)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<com.fitech.app.trainers.application.dto.ClientResourceResponseDto> getResourcesForTrainerClients(Integer trainerId, ServiceResource.ResourceType resourceType) {
        log.info("Getting resources of type {} for trainer's clients: {}", resourceType, trainerId);
        
        List<com.fitech.app.trainers.application.dto.ClientResourceResponseDto> result = new ArrayList<>();
        
        result.addAll(serviceResourceRepository.findActiveResourcesByTrainerClientsAndTypeWithClientInfo(trainerId, resourceType)
                .stream()
                .map(this::mapToClientResourceResponseWithClientInfo)
                .collect(Collectors.toList()));
        
        result.addAll(serviceResourceRepository.findActiveDirectResourcesByTrainerAndTypeWithClientInfo(trainerId, resourceType)
                .stream()
                .map(this::mapToDirectClientResourceResponseWithClientInfo)
                .collect(Collectors.toList()));
        
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ClientResourceGroupDto> getResourcesGroupedByClient(
            Integer trainerId,
            ServiceResource.ResourceType resourceType,
            Pageable pageable) {
        log.info("Getting resources grouped by client for trainer: {} and type: {}", trainerId, resourceType);
        
        List<Object[]> allResources = new ArrayList<>();
        
        allResources.addAll(serviceResourceRepository
            .findActiveResourcesByTrainerClientsAndTypeWithClientInfo(trainerId, resourceType));
        
        allResources.addAll(serviceResourceRepository
            .findActiveDirectResourcesByTrainerAndTypeWithClientInfo(trainerId, resourceType));
        
        Map<Integer, ClientResourceGroupDto> groupedResources = new HashMap<>();

        for (Object[] result : allResources) {
            ServiceResource resource = (ServiceResource) result[0];
            Integer clientId = (Integer) result[1];
            String clientFirstName = (String) result[2];
            String clientLastName = (String) result[3];
            String clientName = clientFirstName + " " + clientLastName;
            
            ClientResourceGroupDto group = groupedResources.computeIfAbsent(clientId,
                k -> new ClientResourceGroupDto(clientId, clientName, new ArrayList<>()));
            
            if (resource.getService() != null) {
                group.getResources().add(mapToClientResourceResponse(resource));
            } else {
                group.getResources().add(mapToDirectClientResourceResponse(resource));
            }
        }
        
        List<ClientResourceGroupDto> sortedGroups = groupedResources.values().stream()
            .sorted(Comparator.comparing(ClientResourceGroupDto::getClientName))
            .collect(Collectors.toList());
        
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), sortedGroups.size());
        
        if (start > sortedGroups.size()) {
            return new PageImpl<>(Collections.emptyList(), pageable, sortedGroups.size());
        }

        List<ClientResourceGroupDto> pageContent = sortedGroups.subList(start, end);
        return new PageImpl<>(pageContent, pageable, sortedGroups.size());
    }

    @Override
    @Transactional(readOnly = true)
    public List<com.fitech.app.trainers.application.dto.ClientResourceResponseDto> getAllResourcesForTrainerClientsIncludingInactive(Integer trainerId) {
        log.info("Getting ALL resources (including inactive) for trainer's clients: {}", trainerId);
        
        return serviceResourceRepository.findAllResourcesByTrainerClientsWithClientInfo(trainerId)
                .stream()
                .map(this::mapToClientResourceResponseWithClientInfo)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<com.fitech.app.trainers.application.dto.ClientResourceResponseDto> getResourcesForTrainerClientsIncludingInactive(Integer trainerId, ServiceResource.ResourceType resourceType) {
        log.info("Getting ALL resources (including inactive) of type {} for trainer's clients: {}", resourceType, trainerId);
        
        return serviceResourceRepository.findAllResourcesByTrainerClientsAndTypeWithClientInfo(trainerId, resourceType)
                .stream()
                .map(this::mapToClientResourceResponseWithClientInfo)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ClientResourceGroupDto> getResourcesGroupedByClientIncludingInactive(
            Integer trainerId,
            ServiceResource.ResourceType resourceType,
            Pageable pageable) {
        log.info("Getting ALL resources (including inactive) grouped by client for trainer: {} and type: {}", trainerId, resourceType);
        
        List<Object[]> allResources = new ArrayList<>();
        
        // Incluir recursos con servicio (activos e inactivos)
        allResources.addAll(serviceResourceRepository
            .findAllResourcesByTrainerClientsAndTypeWithClientInfo(trainerId, resourceType));
        
        // Incluir recursos directos (activos e inactivos)
        allResources.addAll(serviceResourceRepository
            .findAllDirectResourcesByTrainerAndTypeWithClientInfo(trainerId, resourceType));
        
        Map<Integer, ClientResourceGroupDto> groupedResources = new HashMap<>();

        for (Object[] result : allResources) {
            ServiceResource resource = (ServiceResource) result[0];
            Integer clientId = (Integer) result[1];
            String clientFirstName = (String) result[2];
            String clientLastName = (String) result[3];
            String clientName = clientFirstName + " " + clientLastName;
            
            ClientResourceGroupDto group = groupedResources.computeIfAbsent(clientId,
                k -> new ClientResourceGroupDto(clientId, clientName, new ArrayList<>()));
            
            if (resource.getService() != null) {
                group.getResources().add(mapToClientResourceResponse(resource));
            } else {
                group.getResources().add(mapToDirectClientResourceResponse(resource));
            }
        }
        
        List<ClientResourceGroupDto> sortedGroups = groupedResources.values().stream()
            .sorted(Comparator.comparing(ClientResourceGroupDto::getClientName))
            .collect(Collectors.toList());
        
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), sortedGroups.size());
        
        if (start > sortedGroups.size()) {
            return new PageImpl<>(Collections.emptyList(), pageable, sortedGroups.size());
        }

        List<ClientResourceGroupDto> pageContent = sortedGroups.subList(start, end);
        return new PageImpl<>(pageContent, pageable, sortedGroups.size());
    }

    @Transactional(readOnly = true)
    public Long countActiveResourcesByClientId(Integer clientId) {
        return serviceResourceRepository.countByClientIdAndIsActiveTrue(clientId);
    }

    
    @Transactional(readOnly = true)
    public List<ServiceResourceDto> getResourcesByClientId(Integer clientId) {
        return serviceResourceRepository.findByClientIdAndIsActiveTrue(clientId)
                .stream()
                .map(ServiceResourceDto::new)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<ServiceResourceDto> getResourcesByClientIdAndType(Integer clientId, ServiceResource.ResourceType resourceType) {
        return serviceResourceRepository.findByClientIdAndResourceTypeAndIsActiveTrue(clientId, resourceType)
                .stream()
                .map(ServiceResourceDto::new)
                .collect(Collectors.toList());
    }
    
    private com.fitech.app.trainers.application.dto.ClientResourceResponseDto mapToDirectClientResourceResponse(ServiceResource resource) {
        com.fitech.app.trainers.application.dto.ClientResourceResponseDto dto = new com.fitech.app.trainers.application.dto.ClientResourceResponseDto();
        dto.setId(resource.getId());
        dto.setServiceId(null); // Recurso directo, no tiene servicio
        dto.setServiceName("Recurso directo"); // Indicar que es directo
        
        String trainerName = "Trainer no encontrado";
        if (resource.getTrainerId() != null) {
            try {
                Optional<User> trainer = userRepository.findById(resource.getTrainerId());
                if (trainer.isPresent() && trainer.get().getPerson() != null) {
                    trainerName = trainer.get().getPerson().getFirstName() + " " + trainer.get().getPerson().getLastName();
                }
            } catch (Exception e) {
                log.warn("Error getting trainer name for direct resource {}: {}", resource.getId(), e.getMessage());
            }
        }
        dto.setTrainerName(trainerName);
        
        dto.setResourceName(resource.getResourceName());
        dto.setResourceType(resource.getResourceType());
        dto.setResourceObjective(resource.getResourceObjective());
        dto.setResourceDetails(resource.getResourceDetails());
        dto.setStartDate(resource.getStartDate());
        dto.setEndDate(resource.getEndDate());
        dto.setTrainerNotes(resource.getTrainerNotes());
        dto.setCreatedAt(resource.getCreatedAt());
        dto.setUpdatedAt(resource.getUpdatedAt());
        dto.setIsActive(resource.getIsActive());
        return dto;
    }
    
    private com.fitech.app.trainers.application.dto.ClientResourceResponseDto mapToDirectClientResourceResponseWithClientInfo(Object[] result) {
        ServiceResource resource = (ServiceResource) result[0];
        Integer clientId = (Integer) result[1];
        String clientFirstName = (String) result[2];
        String clientLastName = (String) result[3];
        
        com.fitech.app.trainers.application.dto.ClientResourceResponseDto dto = new com.fitech.app.trainers.application.dto.ClientResourceResponseDto();
        dto.setId(resource.getId());
        dto.setServiceId(null); // Recurso directo, no tiene servicio
        dto.setServiceName("Recurso directo"); // Indicar que es directo
        
        String trainerName = "Trainer no encontrado";
        if (resource.getTrainerId() != null) {
            try {
                Optional<User> trainer = userRepository.findById(resource.getTrainerId());
                if (trainer.isPresent() && trainer.get().getPerson() != null) {
                    trainerName = trainer.get().getPerson().getFirstName() + " " + trainer.get().getPerson().getLastName();
                }
            } catch (Exception e) {
                log.warn("Error getting trainer name for direct resource {}: {}", resource.getId(), e.getMessage());
            }
        }
        dto.setTrainerName(trainerName);
        
        dto.setClientId(clientId);
        dto.setClientName(clientFirstName + " " + clientLastName);
        
        dto.setResourceName(resource.getResourceName());
        dto.setResourceType(resource.getResourceType());
        dto.setResourceObjective(resource.getResourceObjective());
        dto.setResourceDetails(resource.getResourceDetails());
        dto.setStartDate(resource.getStartDate());
        dto.setEndDate(resource.getEndDate());
        dto.setTrainerNotes(resource.getTrainerNotes());
        dto.setCreatedAt(resource.getCreatedAt());
        dto.setUpdatedAt(resource.getUpdatedAt());
        dto.setIsActive(resource.getIsActive());
        return dto;
    }
    
    private com.fitech.app.trainers.application.dto.ClientResourceResponseDto mapToUnifiedClientResourceResponseWithClientInfo(Object[] result) {
        ServiceResource resource = (ServiceResource) result[0];
        Integer clientId = (Integer) result[1];
        String clientFirstName = (String) result[2];
        String clientLastName = (String) result[3];
        String resourceOrigin = (String) result[4]; // 'SERVICE' o 'DIRECT'
        
        com.fitech.app.trainers.application.dto.ClientResourceResponseDto dto = new com.fitech.app.trainers.application.dto.ClientResourceResponseDto();
        dto.setId(resource.getId());
        dto.setClientId(clientId);
        dto.setClientName(clientFirstName + " " + clientLastName);
        
        if ("SERVICE".equals(resourceOrigin) && resource.getService() != null) {
            // Recurso a través de servicio
            dto.setServiceId(resource.getServiceId());
            dto.setServiceName(resource.getService().getName());
            
            String trainerName = "Trainer no encontrado";
            try {
                Optional<User> trainer = userRepository.findById(resource.getService().getTrainerId());
                if (trainer.isPresent() && trainer.get().getPerson() != null) {
                    trainerName = trainer.get().getPerson().getFirstName() + " " + trainer.get().getPerson().getLastName();
                }
            } catch (Exception e) {
                log.warn("Error getting trainer name for service {}: {}", resource.getServiceId(), e.getMessage());
            }
            dto.setTrainerName(trainerName);
        } else {
            // Recurso directo
            dto.setServiceId(null);
            dto.setServiceName("Recurso directo");
            
            String trainerName = "Trainer no encontrado";
            if (resource.getTrainerId() != null) {
                try {
                    Optional<User> trainer = userRepository.findById(resource.getTrainerId());
                    if (trainer.isPresent() && trainer.get().getPerson() != null) {
                        trainerName = trainer.get().getPerson().getFirstName() + " " + trainer.get().getPerson().getLastName();
                    }
                } catch (Exception e) {
                    log.warn("Error getting trainer name for direct resource {}: {}", resource.getId(), e.getMessage());
                }
            }
            dto.setTrainerName(trainerName);
        }
        
        dto.setResourceName(resource.getResourceName());
        dto.setResourceType(resource.getResourceType());
        dto.setResourceObjective(resource.getResourceObjective());
        dto.setResourceDetails(resource.getResourceDetails());
        dto.setStartDate(resource.getStartDate());
        dto.setEndDate(resource.getEndDate());
        dto.setTrainerNotes(resource.getTrainerNotes());
        dto.setCreatedAt(resource.getCreatedAt());
        dto.setUpdatedAt(resource.getUpdatedAt());
        dto.setIsActive(resource.getIsActive());
        return dto;
    }
    
    private com.fitech.app.trainers.application.dto.ClientResourceResponseDto mapToClientResourceResponse(ServiceResource resource) {
        com.fitech.app.trainers.application.dto.ClientResourceResponseDto dto = new com.fitech.app.trainers.application.dto.ClientResourceResponseDto();
        dto.setId(resource.getId());
        dto.setServiceId(resource.getServiceId());
        dto.setServiceName(resource.getService().getName());
        
        String trainerName = "Trainer no encontrado";
        try {
            Optional<User> trainer = userRepository.findById(resource.getService().getTrainerId());
            if (trainer.isPresent() && trainer.get().getPerson() != null) {
                trainerName = trainer.get().getPerson().getFirstName() + " " + trainer.get().getPerson().getLastName();
            }
        } catch (Exception e) {
            log.warn("Error getting trainer name for service {}: {}", resource.getServiceId(), e.getMessage());
        }
        dto.setTrainerName(trainerName);
        
        dto.setResourceName(resource.getResourceName());
        dto.setResourceType(resource.getResourceType());
        dto.setResourceObjective(resource.getResourceObjective());
        dto.setResourceDetails(resource.getResourceDetails());
        dto.setStartDate(resource.getStartDate());
        dto.setEndDate(resource.getEndDate());
        dto.setTrainerNotes(resource.getTrainerNotes());
        dto.setCreatedAt(resource.getCreatedAt());
        dto.setUpdatedAt(resource.getUpdatedAt());
        dto.setIsActive(resource.getIsActive());
        return dto;
    }

    // Mapper original para recursos con servicio e información del cliente
    private com.fitech.app.trainers.application.dto.ClientResourceResponseDto mapToClientResourceResponseWithClientInfo(Object[] result) {
        ServiceResource resource = (ServiceResource) result[0];
        Integer clientId = (Integer) result[1];
        String clientFirstName = (String) result[2];
        String clientLastName = (String) result[3];
        
        com.fitech.app.trainers.application.dto.ClientResourceResponseDto dto = new com.fitech.app.trainers.application.dto.ClientResourceResponseDto();
        dto.setId(resource.getId());
        dto.setServiceId(resource.getServiceId());
        dto.setServiceName(resource.getService().getName());
        
        String trainerName = "Trainer no encontrado";
        try {
            Optional<User> trainer = userRepository.findById(resource.getService().getTrainerId());
            if (trainer.isPresent() && trainer.get().getPerson() != null) {
                trainerName = trainer.get().getPerson().getFirstName() + " " + trainer.get().getPerson().getLastName();
            }
        } catch (Exception e) {
            log.warn("Error getting trainer name for service {}: {}", resource.getServiceId(), e.getMessage());
        }
        dto.setTrainerName(trainerName);
        
        dto.setClientId(clientId);
        dto.setClientName(clientFirstName + " " + clientLastName);
        
        dto.setResourceName(resource.getResourceName());
        dto.setResourceType(resource.getResourceType());
        dto.setResourceObjective(resource.getResourceObjective());
        dto.setResourceDetails(resource.getResourceDetails());
        dto.setStartDate(resource.getStartDate());
        dto.setEndDate(resource.getEndDate());
        dto.setTrainerNotes(resource.getTrainerNotes());
        dto.setCreatedAt(resource.getCreatedAt());
        dto.setUpdatedAt(resource.getUpdatedAt());
        dto.setIsActive(resource.getIsActive());
        return dto;
    }
} 