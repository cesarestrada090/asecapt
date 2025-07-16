package com.fitech.app.trainers.infrastructure.repositories;

import com.fitech.app.trainers.domain.entities.ServiceResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceResourceRepository extends JpaRepository<ServiceResource, Integer> {

    /**
     * Buscar todos los recursos activos de un servicio específico
     */
    List<ServiceResource> findByServiceIdAndIsActiveTrue(Integer serviceId);

    /**
     * Buscar todos los recursos de un servicio (activos e inactivos)
     */
    List<ServiceResource> findByServiceId(Integer serviceId);

    /**
     * Buscar todos los recursos activos de un cliente específico (recursos directos)
     */
    List<ServiceResource> findByClientIdAndIsActiveTrue(Integer clientId);

    /**
     * Buscar todos los recursos de un cliente (activos e inactivos) - recursos directos
     */
    List<ServiceResource> findByClientId(Integer clientId);

    /**
     * Buscar recursos por tipo y servicio
     */
    List<ServiceResource> findByServiceIdAndResourceTypeAndIsActiveTrue(
            Integer serviceId, ServiceResource.ResourceType resourceType);

    /**
     * Buscar recursos por tipo y cliente directo
     */
    List<ServiceResource> findByClientIdAndResourceTypeAndIsActiveTrue(
            Integer clientId, ServiceResource.ResourceType resourceType);

    /**
     * Buscar recursos directos por tipo y cliente (sin serviceId)
     */
    List<ServiceResource> findByClientIdAndResourceTypeAndServiceIdIsNullAndIsActiveTrue(
            Integer clientId, ServiceResource.ResourceType resourceType);

    /**
     * Buscar todos los recursos directos de un cliente (sin serviceId)
     */
    List<ServiceResource> findByClientIdAndServiceIdIsNullAndIsActiveTrue(Integer clientId);

    /**
     * Contar recursos activos de un servicio
     */
    Long countByServiceIdAndIsActiveTrue(Integer serviceId);

    /**
     * Contar recursos activos de un cliente directo
     */
    Long countByClientIdAndIsActiveTrue(Integer clientId);

    /**
     * Buscar recursos de todos los servicios de un trainer
     */
    @Query("SELECT sr FROM ServiceResource sr " +
           "JOIN sr.service s " +
           "WHERE s.trainerId = :trainerId AND sr.isActive = true")
    List<ServiceResource> findByTrainerIdAndIsActiveTrue(@Param("trainerId") Integer trainerId);

    /**
     * Buscar recursos directos de un trainer (sin servicio asociado)
     */
    List<ServiceResource> findByTrainerIdAndServiceIdIsNullAndIsActiveTrue(Integer trainerId);

    /**
     * Buscar TODOS los recursos de un trainer (con servicio y directos)
     */
    @Query("SELECT sr FROM ServiceResource sr " +
           "WHERE (sr.trainerId = :trainerId OR " +
           "       (sr.service IS NOT NULL AND sr.service.trainerId = :trainerId)) " +
           "AND sr.isActive = true")
    List<ServiceResource> findAllResourcesByTrainerIdAndIsActiveTrue(@Param("trainerId") Integer trainerId);

    /**
     * Verificar si existe un recurso con el mismo nombre en el servicio
     */
    boolean existsByServiceIdAndResourceNameAndIsActiveTrue(Integer serviceId, String resourceName);

    /**
     * Verificar si existe un recurso con el mismo nombre para el cliente directo
     */
    boolean existsByClientIdAndResourceNameAndIsActiveTrue(Integer clientId, String resourceName);

    /**
     * Check if a resource with the same name exists for a specific service and client
     */
    boolean existsByServiceIdAndClientIdAndResourceNameAndIsActiveTrue(Integer serviceId, Integer clientId, String resourceName);

    /**
     * Buscar todos los recursos activos con información completa (con servicio)
     */
    @Query("SELECT sr FROM ServiceResource sr " +
           "JOIN FETCH sr.service s " +
           "JOIN FETCH s.serviceType st " +
           "WHERE sr.isActive = true AND sr.service IS NOT NULL " +
           "ORDER BY sr.createdAt DESC")
    List<ServiceResource> findAllActiveResourcesWithService();

    /**
     * Buscar todos los recursos activos directos (sin servicio)
     */
    @Query("SELECT sr FROM ServiceResource sr " +
           "WHERE sr.isActive = true AND sr.service IS NULL " +
           "ORDER BY sr.createdAt DESC")
    List<ServiceResource> findAllActiveDirectResources();

    /**
     * Buscar recursos de un tipo específico activos con información completa (con servicio)
     */
    @Query("SELECT sr FROM ServiceResource sr " +
           "JOIN FETCH sr.service s " +
           "JOIN FETCH s.serviceType st " +
           "WHERE sr.resourceType = :resourceType AND sr.isActive = true AND sr.service IS NOT NULL " +
           "ORDER BY sr.createdAt DESC")
    List<ServiceResource> findActiveResourcesByTypeWithService(@Param("resourceType") ServiceResource.ResourceType resourceType);

    /**
     * Buscar recursos de un tipo específico activos directos (sin servicio)
     */
    @Query("SELECT sr FROM ServiceResource sr " +
           "WHERE sr.resourceType = :resourceType AND sr.isActive = true AND sr.service IS NULL " +
           "ORDER BY sr.createdAt DESC")
    List<ServiceResource> findActiveDirectResourcesByType(@Param("resourceType") ServiceResource.ResourceType resourceType);

    /**
     * Buscar todos los recursos activos de los clientes de un trainer con información del cliente (con servicio)
     */
    @Query("SELECT sr, sr.clientId, u.person.firstName, u.person.lastName " +
           "FROM ServiceResource sr " +
           "JOIN sr.service s " +
           "JOIN User u ON u.id = sr.clientId " +
           "WHERE s.trainerId = :trainerId " +
           "AND sr.isActive = true " +
           "AND s.isActive = true " +
           "AND sr.clientId IS NOT NULL " +
           "ORDER BY u.person.firstName, u.person.lastName, sr.createdAt DESC")
    List<Object[]> findActiveResourcesByTrainerClientsWithClientInfo(@Param("trainerId") Integer trainerId);

    /**
     * Buscar recursos directos activos de un trainer con información del cliente
     */
    @Query("SELECT sr, sr.clientId, u.person.firstName, u.person.lastName " +
           "FROM ServiceResource sr " +
           "JOIN User u ON u.id = sr.clientId " +
           "WHERE sr.trainerId = :trainerId " +
           "AND sr.isActive = true " +
           "AND sr.service IS NULL " +
           "ORDER BY u.person.firstName, u.person.lastName, sr.createdAt DESC")
    List<Object[]> findActiveDirectResourcesByTrainerWithClientInfo(@Param("trainerId") Integer trainerId);

    /**
     * Buscar TODOS los recursos activos de un trainer (con servicio y directos) con información del cliente
     */
    @Query("SELECT sr, " +
           "CASE WHEN sr.service IS NOT NULL THEN sc.clientId ELSE sr.clientId END as clientId, " +
           "u.person.firstName, u.person.lastName, " +
           "CASE WHEN sr.service IS NOT NULL THEN 'SERVICE' ELSE 'DIRECT' END as resourceOrigin " +
           "FROM ServiceResource sr " +
           "LEFT JOIN sr.service s " +
           "LEFT JOIN ServiceContract sc ON sc.serviceId = s.id AND sc.contractStatus = 'ACTIVE' " +
           "LEFT JOIN User u ON u.id = CASE WHEN sr.service IS NOT NULL THEN sc.clientId ELSE sr.clientId END " +
           "WHERE ((s.trainerId = :trainerId AND s.isActive = true) OR " +
           "       (sr.trainerId = :trainerId AND sr.service IS NULL)) " +
           "AND sr.isActive = true " +
           "ORDER BY u.person.firstName, u.person.lastName, sr.createdAt DESC")
    List<Object[]> findAllActiveResourcesByTrainerWithClientInfo(@Param("trainerId") Integer trainerId);

    /**
     * Buscar recursos de un tipo específico de los clientes de un trainer con información del cliente (con servicio)
     */
    @Query("SELECT sr, sr.clientId, u.person.firstName, u.person.lastName " +
           "FROM ServiceResource sr " +
           "JOIN sr.service s " +
           "JOIN User u ON u.id = sr.clientId " +
           "WHERE s.trainerId = :trainerId " +
           "AND sr.resourceType = :resourceType " +
           "AND sr.isActive = true " +
           "AND s.isActive = true " +
           "AND sr.clientId IS NOT NULL " +
           "ORDER BY u.person.firstName, u.person.lastName, sr.createdAt DESC")
    List<Object[]> findActiveResourcesByTrainerClientsAndTypeWithClientInfo(
            @Param("trainerId") Integer trainerId,
            @Param("resourceType") ServiceResource.ResourceType resourceType);

    /**
     * Buscar recursos directos de un tipo específico de un trainer con información del cliente
     */
    @Query("SELECT sr, sr.clientId, u.person.firstName, u.person.lastName " +
           "FROM ServiceResource sr " +
           "JOIN User u ON u.id = sr.clientId " +
           "WHERE sr.trainerId = :trainerId " +
           "AND sr.resourceType = :resourceType " +
           "AND sr.isActive = true " +
           "AND sr.service IS NULL " +
           "ORDER BY u.person.firstName, u.person.lastName, sr.createdAt DESC")
    List<Object[]> findActiveDirectResourcesByTrainerAndTypeWithClientInfo(
            @Param("trainerId") Integer trainerId,
            @Param("resourceType") ServiceResource.ResourceType resourceType);

    /**
     * Buscar TODOS los recursos (activos e inactivos) de los clientes de un trainer con información del cliente (con servicio)
     */
    @Query("SELECT sr, sr.clientId, u.person.firstName, u.person.lastName " +
           "FROM ServiceResource sr " +
           "JOIN sr.service s " +
           "JOIN User u ON u.id = sr.clientId " +
           "WHERE s.trainerId = :trainerId " +
           "AND s.isActive = true " +
           "AND sr.clientId IS NOT NULL " +
           "ORDER BY u.person.firstName, u.person.lastName, sr.createdAt DESC")
    List<Object[]> findAllResourcesByTrainerClientsWithClientInfo(@Param("trainerId") Integer trainerId);

    /**
     * Buscar TODOS los recursos (activos e inactivos) de un tipo específico de los clientes de un trainer con información del cliente (con servicio)
     */
    @Query("SELECT sr, sr.clientId, u.person.firstName, u.person.lastName " +
           "FROM ServiceResource sr " +
           "JOIN sr.service s " +
           "JOIN User u ON u.id = sr.clientId " +
           "WHERE s.trainerId = :trainerId " +
           "AND sr.resourceType = :resourceType " +
           "AND s.isActive = true " +
           "AND sr.clientId IS NOT NULL " +
           "ORDER BY u.person.firstName, u.person.lastName, sr.createdAt DESC")
    List<Object[]> findAllResourcesByTrainerClientsAndTypeWithClientInfo(
            @Param("trainerId") Integer trainerId,
            @Param("resourceType") ServiceResource.ResourceType resourceType);

    /**
     * Buscar TODOS los recursos directos (activos e inactivos) de un tipo específico de un trainer con información del cliente
     */
    @Query("SELECT sr, sr.clientId, u.person.firstName, u.person.lastName " +
           "FROM ServiceResource sr " +
           "JOIN User u ON u.id = sr.clientId " +
           "WHERE sr.trainerId = :trainerId " +
           "AND sr.resourceType = :resourceType " +
           "AND sr.service IS NULL " +
           "ORDER BY u.person.firstName, u.person.lastName, sr.createdAt DESC")
    List<Object[]> findAllDirectResourcesByTrainerAndTypeWithClientInfo(
            @Param("trainerId") Integer trainerId,
            @Param("resourceType") ServiceResource.ResourceType resourceType);

    /**
     * Buscar recursos activos de un cliente específico a través de contratos de servicios
     */
    @Query("SELECT sr, sr.clientId, u.person.firstName, u.person.lastName " +
           "FROM ServiceResource sr " +
           "INNER JOIN sr.service s " +
           "INNER JOIN User u ON u.id = sr.clientId " +
           "WHERE sr.clientId = :clientId " +
           "AND sr.resourceType = :resourceType " +
           "AND sr.isActive = true " +
           "AND s.isActive = true " +
           "ORDER BY sr.createdAt DESC")
    List<Object[]> findActiveResourcesByClientAndTypeWithClientInfo(
            @Param("clientId") Integer clientId,
            @Param("resourceType") ServiceResource.ResourceType resourceType);

    /**
     * Buscar todos los recursos activos de un cliente específico a través de contratos de servicios
     */
    @Query("SELECT sr, sr.clientId, u.person.firstName, u.person.lastName " +
           "FROM ServiceResource sr " +
           "INNER JOIN sr.service s " +
           "INNER JOIN User u ON u.id = sr.clientId " +
           "WHERE sr.clientId = :clientId " +
           "AND sr.isActive = true " +
           "AND s.isActive = true " +
           "ORDER BY sr.createdAt DESC")
    List<Object[]> findActiveResourcesByClientWithClientInfo(
            @Param("clientId") Integer clientId);
} 