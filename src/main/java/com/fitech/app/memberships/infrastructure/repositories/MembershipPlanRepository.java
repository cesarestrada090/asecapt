package com.fitech.app.memberships.infrastructure.repositories;

import com.fitech.app.memberships.domain.entities.MembershipPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MembershipPlanRepository extends JpaRepository<MembershipPlan, Long> {

    // Encontrar planes activos ordenados por orden de visualización
    List<MembershipPlan> findByIsActiveTrueOrderByDisplayOrderAsc();

    // Encontrar planes por ciclo de facturación
    List<MembershipPlan> findByBillingCycleAndIsActiveTrueOrderByPriceAsc(MembershipPlan.BillingCycle billingCycle);

    // Encontrar plan por nombre
    Optional<MembershipPlan> findByNameAndIsActiveTrue(String name);

    // Encontrar planes mensuales activos
    @Query("SELECT mp FROM MembershipPlan mp WHERE mp.billingCycle = 'MONTHLY' AND mp.isActive = true ORDER BY mp.price ASC")
    List<MembershipPlan> findActiveMonthlyPlans();

    // Encontrar planes anuales activos
    @Query("SELECT mp FROM MembershipPlan mp WHERE mp.billingCycle = 'ANNUAL' AND mp.isActive = true ORDER BY mp.price ASC")
    List<MembershipPlan> findActiveAnnualPlans();

    // Encontrar planes en un rango de precio
    @Query("SELECT mp FROM MembershipPlan mp WHERE mp.price BETWEEN :minPrice AND :maxPrice AND mp.isActive = true ORDER BY mp.price ASC")
    List<MembershipPlan> findPlansByPriceRange(@Param("minPrice") java.math.BigDecimal minPrice, @Param("maxPrice") java.math.BigDecimal maxPrice);

    // Encontrar planes con soporte prioritario
    List<MembershipPlan> findByPrioritySupportTrueAndIsActiveTrueOrderByPriceAsc();

    // Contar planes activos
    @Query("SELECT COUNT(mp) FROM MembershipPlan mp WHERE mp.isActive = true")
    long countActivePlans();

    // Encontrar el plan más barato
    @Query("SELECT mp FROM MembershipPlan mp WHERE mp.isActive = true ORDER BY mp.price ASC LIMIT 1")
    Optional<MembershipPlan> findCheapestPlan();

    // Encontrar el plan más caro
    @Query("SELECT mp FROM MembershipPlan mp WHERE mp.isActive = true ORDER BY mp.price DESC LIMIT 1")
    Optional<MembershipPlan> findMostExpensivePlan();

    // Encontrar planes populares (con más suscripciones activas)
    @Query("SELECT mp, COUNT(um) as subscriptions FROM MembershipPlan mp " +
           "LEFT JOIN UserMembership um ON mp.id = um.planId AND um.status = 'ACTIVE' " +
           "WHERE mp.isActive = true " +
           "GROUP BY mp.id " +
           "ORDER BY subscriptions DESC")
    List<Object[]> findPopularPlans();

    // Buscar planes por características
    @Query("SELECT mp FROM MembershipPlan mp WHERE mp.features LIKE %:feature% AND mp.isActive = true")
    List<MembershipPlan> findPlansByFeature(@Param("feature") String feature);
} 