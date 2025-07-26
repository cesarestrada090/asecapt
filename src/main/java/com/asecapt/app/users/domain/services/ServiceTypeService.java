package com.asecapt.app.users.domain.services;

import com.asecapt.app.users.application.dto.ServiceTypeDTO;
import com.asecapt.app.users.domain.entities.ServiceType;
import com.asecapt.app.users.infrastructure.repository.ServiceTypeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ServiceTypeService {
    
    private final ServiceTypeRepository serviceTypeRepository;
    
    public ServiceTypeService(ServiceTypeRepository serviceTypeRepository) {
        this.serviceTypeRepository = serviceTypeRepository;
    }
    
    /**
     * Obtiene todos los tipos de servicios activos
     */
    @Transactional(readOnly = true)
    public List<ServiceTypeDTO> getAllActiveServiceTypes() {
        return serviceTypeRepository.findByIsActiveTrueOrderByName()
                .stream()
                .map(ServiceTypeDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    /**
     * Busca un tipo de servicio por ID
     */
    @Transactional(readOnly = true)
    public Optional<ServiceTypeDTO> getServiceTypeById(Integer id) {
        return serviceTypeRepository.findById(id)
                .filter(ServiceType::getIsActive)
                .map(ServiceTypeDTO::fromEntity);
    }
    
    /**
     * Busca un tipo de servicio por nombre
     */
    @Transactional(readOnly = true)
    public Optional<ServiceTypeDTO> getServiceTypeByName(String name) {
        return serviceTypeRepository.findByNameAndIsActiveTrue(name)
                .map(ServiceTypeDTO::fromEntity);
    }
    
    /**
     * Verifica si existe un tipo de servicio
     */
    @Transactional(readOnly = true)
    public boolean existsServiceType(String name) {
        return serviceTypeRepository.existsByNameAndIsActiveTrue(name);
    }
    
    /**
     * Obtiene la entidad ServiceType por ID (para uso interno)
     */
    @Transactional(readOnly = true)
    public Optional<ServiceType> getServiceTypeEntityById(Integer id) {
        return serviceTypeRepository.findById(id)
                .filter(ServiceType::getIsActive);
    }
} 