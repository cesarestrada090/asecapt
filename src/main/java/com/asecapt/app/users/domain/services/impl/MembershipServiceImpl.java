package com.asecapt.app.users.domain.services.impl;

import com.asecapt.app.users.domain.services.MembershipService;
import com.asecapt.app.users.domain.entities.UserMembership;
import com.asecapt.app.users.domain.entities.MembershipPayment;
import com.asecapt.app.users.domain.entities.MembershipPlan;
import com.asecapt.app.users.infrastructure.repository.UserMembershipRepository;
import com.asecapt.app.users.infrastructure.repository.MembershipPaymentRepository;
import com.asecapt.app.users.infrastructure.repository.MembershipPlanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MembershipServiceImpl implements MembershipService {

    private final UserMembershipRepository userMembershipRepository;
    private final MembershipPaymentRepository membershipPaymentRepository;
    private final MembershipPlanRepository membershipPlanRepository;

    @Override
    public UserMembership createPaymentMembership(Integer userId, Long planId, BigDecimal amount, String paymentMethod) {
        log.info("Creating payment membership for user {} with plan {}", userId, planId);

        // Obtener el plan
        MembershipPlan plan = membershipPlanRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("Plan no encontrado: " + planId));

        // Crear la membresía
        UserMembership membership = new UserMembership();
        membership.setUserId(userId);
        membership.setMembershipType(UserMembership.MembershipType.PAYMENT);
        membership.setStatus(UserMembership.MembershipStatus.ACTIVE);
        membership.setStartDate(LocalDate.now());
        membership.setEndDate(LocalDate.now().plusDays(plan.getDurationDays()));
        membership.setPlanId(planId);
        membership.setPaymentAmount(amount);
        membership.setCurrency(plan.getCurrency());
        membership.setAutoRenewal(false);

        UserMembership savedMembership = userMembershipRepository.save(membership);

        // Crear el registro de pago
        createPayment(savedMembership.getId(), userId, amount, paymentMethod);

        log.info("Payment membership created successfully: {}", savedMembership.getId());
        return savedMembership;
    }

    @Override
    public UserMembership createContractMembership(Integer userId, Integer trainerId, LocalDate startDate, LocalDate endDate, String contractDetails) {
        log.info("Creating contract membership for user {} with trainer {}", userId, trainerId);

        UserMembership membership = new UserMembership();
        membership.setUserId(userId);
        membership.setMembershipType(UserMembership.MembershipType.CONTRACT);
        membership.setStatus(UserMembership.MembershipStatus.ACTIVE);
        membership.setStartDate(startDate);
        membership.setEndDate(endDate);
        membership.setTrainerId(trainerId);
        membership.setContractDetails(contractDetails);

        UserMembership savedMembership = userMembershipRepository.save(membership);
        log.info("Contract membership created successfully: {}", savedMembership.getId());
        return savedMembership;
    }

    @Override
    public UserMembership createContractMembership(Integer userId, Integer trainerId, LocalDate startDate, LocalDate endDate, String contractDetails, BigDecimal amount, String paymentMethod) {
        log.info("Creating contract membership with payment for user {} with trainer {}", userId, trainerId);

        UserMembership membership = new UserMembership();
        membership.setUserId(userId);
        membership.setMembershipType(UserMembership.MembershipType.CONTRACT);
        membership.setStatus(UserMembership.MembershipStatus.ACTIVE);
        membership.setStartDate(startDate);
        membership.setEndDate(endDate);
        membership.setTrainerId(trainerId);
        membership.setContractDetails(contractDetails);
        membership.setPaymentAmount(amount);
        membership.setCurrency("EUR"); // Moneda por defecto

        UserMembership savedMembership = userMembershipRepository.save(membership);

        // Crear el registro de pago para el contrato
        createPayment(savedMembership.getId(), userId, amount, paymentMethod);

        log.info("Contract membership with payment created successfully: {}", savedMembership.getId());
        return savedMembership;
    }

    @Override
    public UserMembership createContractMembership(Integer userId, Integer trainerId, LocalDate startDate, LocalDate endDate, String contractDetails, BigDecimal amount, String paymentMethod, Integer serviceContractId) {
        log.info("Creating contract membership with payment for user {} with trainer {} and service contract {}", userId, trainerId, serviceContractId);

        UserMembership membership = new UserMembership();
        membership.setUserId(userId);
        membership.setMembershipType(UserMembership.MembershipType.CONTRACT);
        membership.setStatus(UserMembership.MembershipStatus.ACTIVE);
        membership.setStartDate(startDate);
        membership.setEndDate(endDate);
        membership.setTrainerId(trainerId);
        membership.setContractDetails(contractDetails);
        membership.setPaymentAmount(amount);
        membership.setCurrency("EUR"); // Moneda por defecto

        UserMembership savedMembership = userMembershipRepository.save(membership);

        // Crear el registro de pago para el contrato con service contract ID
        createPayment(savedMembership.getId(), userId, amount, paymentMethod, serviceContractId);

        log.info("Contract membership with payment and service contract ID created successfully: {}", savedMembership.getId());
        return savedMembership;
    }

    @Override
    public UserMembership renewMembership(Long membershipId) {
        log.info("Renewing membership: {}", membershipId);

        UserMembership membership = userMembershipRepository.findById(membershipId)
                .orElseThrow(() -> new RuntimeException("Membresía no encontrada: " + membershipId));

        if (membership.getMembershipType() == UserMembership.MembershipType.PAYMENT && membership.getPlanId() != null) {
            MembershipPlan plan = membershipPlanRepository.findById(membership.getPlanId())
                    .orElseThrow(() -> new RuntimeException("Plan no encontrado: " + membership.getPlanId()));

            membership.setEndDate(membership.getEndDate().plusDays(plan.getDurationDays()));
            membership.setStatus(UserMembership.MembershipStatus.ACTIVE);
        }

        return userMembershipRepository.save(membership);
    }

    @Override
    public void cancelMembership(Long membershipId) {
        log.info("Cancelling membership: {}", membershipId);

        UserMembership membership = userMembershipRepository.findById(membershipId)
                .orElseThrow(() -> new RuntimeException("Membresía no encontrada: " + membershipId));

        membership.setStatus(UserMembership.MembershipStatus.CANCELLED);
        userMembershipRepository.save(membership);
    }

    @Override
    public void expireMembership(Long membershipId) {
        log.info("Expiring membership: {}", membershipId);

        UserMembership membership = userMembershipRepository.findById(membershipId)
                .orElseThrow(() -> new RuntimeException("Membresía no encontrada: " + membershipId));

        membership.setStatus(UserMembership.MembershipStatus.EXPIRED);
        userMembershipRepository.save(membership);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserMembership> getActiveMembership(Integer userId) {
        return userMembershipRepository.findActiveByUserId(userId, LocalDate.now());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserMembership> getUserMemberships(Integer userId) {
        return userMembershipRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasActiveMembership(Integer userId) {
        return userMembershipRepository.hasActiveMembership(userId, LocalDate.now());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasActiveContractWithTrainer(Integer userId, Integer trainerId) {
        return userMembershipRepository.findActiveByUserIdAndTrainerId(userId, trainerId, LocalDate.now()).isPresent();
    }

    @Override
    public MembershipPayment createPayment(Long membershipId, Integer userId, BigDecimal amount, String paymentMethod) {
        return createPayment(membershipId, userId, amount, paymentMethod, null);
    }

    public MembershipPayment createPayment(Long membershipId, Integer userId, BigDecimal amount, String paymentMethod, Integer serviceContractId) {
        log.info("Creating payment for membership {} and user {} with service contract {}", membershipId, userId, serviceContractId);

        MembershipPayment payment = new MembershipPayment();
        payment.setMembershipId(membershipId);
        payment.setServiceContractId(serviceContractId);
        payment.setUserId(userId);
        payment.setAmount(amount);
        payment.setPaymentMethod(paymentMethod);
        
        // Contract payments start as PENDING until contract is completed by client
        if (MembershipPayment.METHOD_CONTRACT_PAYMENT.equals(paymentMethod)) {
            payment.setStatus(MembershipPayment.STATUS_COMPLETED);
            payment.setCollectionStatus(MembershipPayment.COLLECTION_PENDING_CLIENT_APPROVAL);
            payment.setDescription("Contract payment - Pending service completion");
            payment.setTransactionId("CONTRACT_" + System.currentTimeMillis());
        } else {
            // Regular payment memberships are marked as COMPLETED immediately
            // But collection status starts as PENDING_CLIENT_APPROVAL
            payment.setStatus(MembershipPayment.STATUS_COMPLETED);
            payment.setCollectionStatus(MembershipPayment.COLLECTION_COLLECTED);
            payment.markPaymentAsCompleted("PAYMENT_" + System.currentTimeMillis());
            payment.setDescription("Premium membership payment");
        }

        MembershipPayment savedPayment = membershipPaymentRepository.save(payment);
        log.info("Payment created with ID: {} for membership: {} with status: {}", 
                savedPayment.getId(), membershipId, savedPayment.getStatus());
        
        return savedPayment;
    }

    @Override
    public void completePayment(Long paymentId, String transactionId) {
        log.info("Completing payment: {} with transaction: {}", paymentId, transactionId);

        MembershipPayment payment = membershipPaymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Pago no encontrado: " + paymentId));

        payment.markAsCollected(transactionId);
        membershipPaymentRepository.save(payment);
        
        log.info("Payment {} marked as COLLECTED", paymentId);
    }

    @Override
    public void failPayment(Long paymentId, String reason) {
        log.info("Marking payment as observed: {} with reason: {}", paymentId, reason);

        MembershipPayment payment = membershipPaymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Pago no encontrado: " + paymentId));

        payment.markAsObserved(reason);
        membershipPaymentRepository.save(payment);
        
        log.info("Payment {} marked as OBSERVED with reason: {}", paymentId, reason);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MembershipPayment> getUserPayments(Integer userId) {
        return membershipPaymentRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MembershipPlan> getActivePlans() {
        return membershipPlanRepository.findByIsActiveTrueOrderByDisplayOrderAsc();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MembershipPlan> getMonthlyPlans() {
        return membershipPlanRepository.findActiveMonthlyPlans();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MembershipPlan> getAnnualPlans() {
        return membershipPlanRepository.findActiveAnnualPlans();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MembershipPlan> getPlanById(Long planId) {
        return membershipPlanRepository.findById(planId);
    }

    @Override
    public void updateExpiredMemberships() {
        log.info("Updating expired memberships");

        List<UserMembership> expiredMemberships = userMembershipRepository.findExpiredActive(LocalDate.now());
        for (UserMembership membership : expiredMemberships) {
            membership.setStatus(UserMembership.MembershipStatus.EXPIRED);
            userMembershipRepository.save(membership);
        }

        log.info("Updated {} expired memberships", expiredMemberships.size());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserMembership> getExpiringMemberships(int daysAhead) {
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusDays(daysAhead);
        return userMembershipRepository.findExpiringBetween(startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalPaidByUser(Integer userId) {
        return membershipPaymentRepository.getTotalPaidByUser(userId);
    }

    @Override
    @Transactional
    public UserMembership reactivateExpiredMembership(Integer userId, String paymentMethod) {
        log.info("Attempting to reactivate expired membership for user: {}", userId);
        
        // Buscar la última membresía del usuario (expirada o cancelada)
        Optional<UserMembership> latestMembership = getLatestMembership(userId);
        
        if (latestMembership.isEmpty()) {
            throw new RuntimeException("No se encontró ninguna membresía previa para el usuario: " + userId);
        }
        
        UserMembership membership = latestMembership.get();
        
        // Solo reactivar si está expirada o cancelada
        if (membership.getStatus() == UserMembership.MembershipStatus.ACTIVE) {
            throw new RuntimeException("La membresía del usuario ya está activa");
        }
        
        // Solo funciona para membresías de pago
        if (membership.getMembershipType() != UserMembership.MembershipType.PAYMENT || membership.getPlanId() == null) {
            throw new RuntimeException("Solo se pueden reactivar membresías de pago");
        }
        
        // Obtener el plan para calcular la nueva fecha de vencimiento
        MembershipPlan plan = membershipPlanRepository.findById(membership.getPlanId())
                .orElseThrow(() -> new RuntimeException("Plan no encontrado: " + membership.getPlanId()));
        
        // Reactivar la membresía
        membership.setStatus(UserMembership.MembershipStatus.ACTIVE);
        membership.setStartDate(LocalDate.now());
        membership.setEndDate(LocalDate.now().plusDays(plan.getDurationDays()));
        membership.setUpdatedAt(LocalDateTime.now());
        
        UserMembership reactivatedMembership = userMembershipRepository.save(membership);
        
        // Crear el registro de pago para la reactivación
        createPayment(reactivatedMembership.getId(), userId, membership.getPaymentAmount(), paymentMethod);
        
        log.info("Membership reactivated successfully: {} for user: {}", reactivatedMembership.getId(), userId);
        return reactivatedMembership;
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<UserMembership> getLatestMembership(Integer userId) {
        List<UserMembership> memberships = userMembershipRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return memberships.isEmpty() ? Optional.empty() : Optional.of(memberships.get(0));
    }
} 