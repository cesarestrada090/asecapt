package com.fitech.app.trainers.domain.services;

import com.fitech.app.trainers.application.dto.ServiceResourceDto;
import com.fitech.app.trainers.application.dto.ClientResourceResponseDto;
import com.fitech.app.trainers.application.dto.ClientResourceGroupDto;
import com.fitech.app.trainers.domain.entities.ServiceResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ServiceResourceService {

    /**
     * Crear un nuevo recurso para un servicio
     */
    ServiceResourceDto createResource(ServiceResourceDto resourceDto);

    /**
     * Actualizar un recurso existente
     */
    ServiceResourceDto updateResource(Integer resourceId, ServiceResourceDto resourceDto);

    /**
     * Obtener un recurso por ID
     */
    Optional<ServiceResourceDto> getResourceById(Integer resourceId);

    /**
     * Obtener todos los recursos activos de un servicio
     */
    List<ServiceResourceDto> getResourcesByServiceId(Integer serviceId);

    /**
     * Obtener recursos por tipo y servicio
     */
    List<ServiceResourceDto> getResourcesByServiceIdAndType(Integer serviceId, ServiceResource.ResourceType resourceType);

    /**
     * Obtener todos los recursos de un trainer
     */
    List<ServiceResourceDto> getResourcesByTrainerId(Integer trainerId);

    /**
     * Desactivar un recurso (soft delete)
     */
    void deactivateResource(Integer resourceId);

    /**
     * Reactivar un recurso
     */
    void activateResource(Integer resourceId);

    /**
     * Eliminar permanentemente un recurso
     */
    void deleteResource(Integer resourceId);

    /**
     * Contar recursos activos de un servicio
     */
    Long countActiveResourcesByServiceId(Integer serviceId);

    /**
     * Verificar si el trainer es propietario del recurso
     */
    boolean isResourceOwnedByTrainer(Integer resourceId, Integer trainerId);

    /**
     * Obtener recursos del cliente autenticado
     */
    List<com.fitech.app.trainers.application.dto.ClientResourceResponseDto> getResourcesForClient(Integer clientId, ServiceResource.ResourceType resourceType);

    /**
     * Obtener todos los recursos del cliente autenticado
     */
    List<com.fitech.app.trainers.application.dto.ClientResourceResponseDto> getAllResourcesForClient(Integer clientId);

    /**
     * Obtener todos los recursos de los clientes de un trainer
     */
    List<com.fitech.app.trainers.application.dto.ClientResourceResponseDto> getAllResourcesForTrainerClients(Integer trainerId);

    /**
     * Obtener recursos específicos (por tipo) de los clientes de un trainer
     */
    List<com.fitech.app.trainers.application.dto.ClientResourceResponseDto> getResourcesForTrainerClients(Integer trainerId, ServiceResource.ResourceType resourceType);

    /**
     * Obtener recursos agrupados por cliente con paginación
     */
    Page<ClientResourceGroupDto> getResourcesGroupedByClient(
        Integer trainerId, 
        ServiceResource.ResourceType resourceType, 
        Pageable pageable
    );

    /**
     * Obtener TODOS los recursos (activos e inactivos) de los clientes de un trainer
     */
    List<com.fitech.app.trainers.application.dto.ClientResourceResponseDto> getAllResourcesForTrainerClientsIncludingInactive(Integer trainerId);

    /**
     * Obtener TODOS los recursos (activos e inactivos) específicos (por tipo) de los clientes de un trainer
     */
    List<com.fitech.app.trainers.application.dto.ClientResourceResponseDto> getResourcesForTrainerClientsIncludingInactive(Integer trainerId, ServiceResource.ResourceType resourceType);

    /**
     * Obtener TODOS los recursos (activos e inactivos) agrupados por cliente con paginación
     */
    Page<ClientResourceGroupDto> getResourcesGroupedByClientIncludingInactive(
        Integer trainerId, 
        ServiceResource.ResourceType resourceType, 
        Pageable pageable
    );

    // ========== MÉTODOS PARA RECURSOS DIRECTOS DE CLIENTES ==========

    /**
     * Contar recursos activos de un cliente directo
     */
    Long countActiveResourcesByClientId(Integer clientId);

    /**
     * Obtener recursos por cliente directo
     */
    List<ServiceResourceDto> getResourcesByClientId(Integer clientId);

    /**
     * Obtener recursos por cliente directo y tipo
     */
    List<ServiceResourceDto> getResourcesByClientIdAndType(Integer clientId, ServiceResource.ResourceType resourceType);
} 