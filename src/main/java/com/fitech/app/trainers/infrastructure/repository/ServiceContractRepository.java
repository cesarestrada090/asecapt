package com.fitech.app.trainers.infrastructure.repository;

import com.fitech.app.trainers.domain.entities.ServiceContract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceContractRepository extends JpaRepository<ServiceContract, Integer> {
    
    List<ServiceContract> findByClientIdOrderByCreatedAtDesc(Integer clientId);
    
    
    List<ServiceContract> findByTrainerIdOrderByCreatedAtDesc(Integer trainerId);
    
    
    List<ServiceContract> findByServiceIdOrderByCreatedAtDesc(Integer serviceId);
    
    
    List<ServiceContract> findByContractStatusOrderByCreatedAtDesc(ServiceContract.ContractStatus status);
    
    
    @Query("SELECT sc FROM ServiceContract sc WHERE sc.clientId = :clientId AND sc.serviceId = :serviceId " +
           "AND sc.contractStatus IN ('ACTIVE', 'COMPLETED')")
    Optional<ServiceContract> findActiveContractByClientAndService(@Param("clientId") Integer clientId, 
                                                                  @Param("serviceId") Integer serviceId);
    
    @Query("SELECT sc FROM ServiceContract sc WHERE sc.clientId = :clientId " +
           "AND sc.contractStatus IN ('PENDING', 'ACTIVE') ORDER BY sc.createdAt DESC")
    List<ServiceContract> findActiveContractsByClient(@Param("clientId") Integer clientId);
    
    @Query("SELECT sc FROM ServiceContract sc WHERE sc.trainerId = :trainerId " +
           "AND sc.contractStatus IN ('PENDING', 'ACTIVE') ORDER BY sc.createdAt DESC")
    List<ServiceContract> findActiveContractsByTrainer(@Param("trainerId") Integer trainerId);
    
    @Query("SELECT COUNT(sc) FROM ServiceContract sc WHERE sc.serviceId = :serviceId " +
           "AND sc.contractStatus IN ('ACTIVE', 'COMPLETED')")
    Long countContractsByService(@Param("serviceId") Integer serviceId);
    
    @Query("SELECT sc FROM ServiceContract sc JOIN FETCH sc.service WHERE sc.clientId = :clientId " +
           "ORDER BY sc.createdAt DESC")
    List<ServiceContract> findByClientIdWithService(@Param("clientId") Integer clientId);
    
    @Query("SELECT sc FROM ServiceContract sc JOIN FETCH sc.service WHERE sc.trainerId = :trainerId " +
           "ORDER BY sc.createdAt DESC")
    List<ServiceContract> findByTrainerIdWithService(@Param("trainerId") Integer trainerId);
    
    @Query("SELECT sc FROM ServiceContract sc JOIN FETCH sc.service WHERE sc.clientId = :clientId " +
           "AND sc.contractStatus IN ('COMPLETED', 'CANCELLED') ORDER BY sc.createdAt DESC")
    List<ServiceContract> findInactiveContractsByClient(@Param("clientId") Integer clientId);
    
    @Query("SELECT sc FROM ServiceContract sc JOIN FETCH sc.service WHERE sc.clientId = :clientId " +
           "AND sc.contractStatus = :status ORDER BY sc.createdAt DESC")
    List<ServiceContract> findContractsByClientAndStatus(@Param("clientId") Integer clientId, 
                                                         @Param("status") ServiceContract.ContractStatus status);
    
    @Query("SELECT sc, p.firstName, p.lastName, p.email, p.phoneNumber, p.profilePhotoId, p.id " +
           "FROM ServiceContract sc " +
           "JOIN FETCH sc.service " +
           "JOIN User u ON u.id = sc.clientId " +
           "JOIN u.person p " +
           "WHERE sc.trainerId = :trainerId " +
           "ORDER BY sc.createdAt DESC")
    List<Object[]> findByTrainerIdWithServiceAndClientInfo(@Param("trainerId") Integer trainerId);
    
    @Query("SELECT p.id, fgt.name " +
           "FROM Person p " +
           "JOIN p.fitnessGoalTypes fgt " +
           "WHERE p.id IN :personIds")
    List<Object[]> findFitnessGoalsByPersonIds(@Param("personIds") List<Integer> personIds);
    
    @Query("SELECT st.name, " +
           "CONCAT(tp.firstName, ' ', tp.lastName), " +
           "CAST(sc.contractStatus AS string), " +
           "CASE WHEN s.isInPerson = true THEN 'presencial' ELSE 'remoto' END, " +
           "sc.createdAt, " +
           "sc.totalAmount " +
           "FROM ServiceContract sc " +
           "JOIN sc.service s " +
           "JOIN s.serviceType st " +
           "JOIN User tu ON tu.id = sc.trainerId " +
           "JOIN tu.person tp " +
           "WHERE sc.clientId = :clientId " +
           "ORDER BY sc.createdAt DESC")
    List<Object[]> findClientServicesWithTrainerInfo(@Param("clientId") Integer clientId);
} 