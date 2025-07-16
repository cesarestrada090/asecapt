package com.fitech.app.memberships.infrastructure.repositories;

import com.fitech.app.memberships.domain.entities.UserMembership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserMembershipRepository extends JpaRepository<UserMembership, Long> {

    // Encontrar membresía activa de un usuario (la más reciente por fecha de creación)
    @Query("SELECT um FROM UserMembership um WHERE um.userId = :userId AND um.status = 'ACTIVE' AND um.endDate > :currentDate ORDER BY um.createdAt DESC LIMIT 1")
    Optional<UserMembership> findActiveByUserId(@Param("userId") Integer userId, @Param("currentDate") LocalDate currentDate);

    // Encontrar todas las membresías de un usuario
    List<UserMembership> findByUserIdOrderByCreatedAtDesc(Integer userId);

    // Encontrar membresías por tipo
    List<UserMembership> findByUserIdAndMembershipTypeOrderByCreatedAtDesc(Integer userId, UserMembership.MembershipType membershipType);

    // Encontrar membresías activas por tipo
    @Query("SELECT um FROM UserMembership um WHERE um.userId = :userId AND um.membershipType = :type AND um.status = 'ACTIVE' AND um.endDate > :currentDate")
    List<UserMembership> findActiveByUserIdAndType(@Param("userId") Integer userId, 
                                                   @Param("type") UserMembership.MembershipType type, 
                                                   @Param("currentDate") LocalDate currentDate);

    // Encontrar membresías que expiran pronto
    @Query("SELECT um FROM UserMembership um WHERE um.status = 'ACTIVE' AND um.endDate BETWEEN :startDate AND :endDate")
    List<UserMembership> findExpiringBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    // Encontrar membresías expiradas que necesitan actualización de estado
    @Query("SELECT um FROM UserMembership um WHERE um.status = 'ACTIVE' AND um.endDate < :currentDate")
    List<UserMembership> findExpiredActive(@Param("currentDate") LocalDate currentDate);

    // Encontrar membresía activa por trainer (para contratos)
    @Query("SELECT um FROM UserMembership um WHERE um.userId = :userId AND um.trainerId = :trainerId AND um.status = 'ACTIVE' AND um.endDate > :currentDate")
    Optional<UserMembership> findActiveByUserIdAndTrainerId(@Param("userId") Integer userId, 
                                                           @Param("trainerId") Integer trainerId, 
                                                           @Param("currentDate") LocalDate currentDate);

    // Verificar si el usuario tiene membresía activa
    @Query("SELECT COUNT(um) > 0 FROM UserMembership um WHERE um.userId = :userId AND um.status = 'ACTIVE' AND um.endDate > :currentDate")
    boolean hasActiveMembership(@Param("userId") Integer userId, @Param("currentDate") LocalDate currentDate);

    // Contar membresías activas por plan
    @Query("SELECT COUNT(um) FROM UserMembership um WHERE um.planId = :planId AND um.status = 'ACTIVE' AND um.endDate > :currentDate")
    long countActiveByPlanId(@Param("planId") Long planId, @Param("currentDate") LocalDate currentDate);

    // Obtener estadísticas de membresías
    @Query("SELECT um.status, COUNT(um) FROM UserMembership um GROUP BY um.status")
    List<Object[]> getMembershipStatistics();
} 