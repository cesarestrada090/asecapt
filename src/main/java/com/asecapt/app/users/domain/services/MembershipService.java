package com.asecapt.app.users.domain.services;

import com.asecapt.app.users.domain.entities.UserMembership;
import com.asecapt.app.users.domain.entities.MembershipPayment;
import com.asecapt.app.users.domain.entities.MembershipPlan;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MembershipService {

    // Gestión de membresías
    UserMembership createPaymentMembership(Integer userId, Long planId, BigDecimal amount, String paymentMethod);
    UserMembership createContractMembership(Integer userId, Integer trainerId, LocalDate startDate, LocalDate endDate, String contractDetails);
    
    // Método sobrecargado para crear membresías de contrato con información de pago
    UserMembership createContractMembership(Integer userId, Integer trainerId, LocalDate startDate, LocalDate endDate, String contractDetails, BigDecimal amount, String paymentMethod);
    
    // Método sobrecargado para crear membresías de contrato con información de pago y service contract ID
    UserMembership createContractMembership(Integer userId, Integer trainerId, LocalDate startDate, LocalDate endDate, String contractDetails, BigDecimal amount, String paymentMethod, Integer serviceContractId);
    UserMembership renewMembership(Long membershipId);
    void cancelMembership(Long membershipId);
    void expireMembership(Long membershipId);

    // Consultas de membresías
    Optional<UserMembership> getActiveMembership(Integer userId);
    List<UserMembership> getUserMemberships(Integer userId);
    boolean hasActiveMembership(Integer userId);
    boolean hasActiveContractWithTrainer(Integer userId, Integer trainerId);

    // Gestión de pagos
    MembershipPayment createPayment(Long membershipId, Integer userId, BigDecimal amount, String paymentMethod);
    void completePayment(Long paymentId, String transactionId);
    void failPayment(Long paymentId, String reason);
    List<MembershipPayment> getUserPayments(Integer userId);

    // Gestión de planes
    List<MembershipPlan> getActivePlans();
    List<MembershipPlan> getMonthlyPlans();
    List<MembershipPlan> getAnnualPlans();
    Optional<MembershipPlan> getPlanById(Long planId);

    // Utilidades
    void updateExpiredMemberships();
    List<UserMembership> getExpiringMemberships(int daysAhead);
    BigDecimal getTotalPaidByUser(Integer userId);

    // Método para reactivar una membresía expirada
    UserMembership reactivateExpiredMembership(Integer userId, String paymentMethod);
    
    // Método para obtener la última membresía de un usuario (activa o expirada)
    Optional<UserMembership> getLatestMembership(Integer userId);
} 