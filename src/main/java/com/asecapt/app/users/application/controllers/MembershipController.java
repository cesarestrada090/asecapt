package com.asecapt.app.users.application.controllers;

import com.asecapt.app.users.domain.services.MembershipService;
import com.asecapt.app.users.domain.entities.UserMembership;
import com.asecapt.app.users.domain.entities.MembershipPayment;
import com.asecapt.app.users.domain.entities.MembershipPlan;
import com.asecapt.app.users.domain.entities.User;
import com.asecapt.app.users.domain.services.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/v1/app/memberships")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
@Tag(name = "Memberships", description = "Premium membership management and subscription operations")
@SecurityRequirement(name = "bearerAuth")
public class MembershipController {

    private final MembershipService membershipService;
    private final UserService userService;

    // Endpoints para planes
    @GetMapping("/plans")
    public ResponseEntity<List<MembershipPlan>> getActivePlans() {
        List<MembershipPlan> plans = membershipService.getActivePlans();
        return ResponseEntity.ok(plans);
    }

    @GetMapping("/plans/monthly")
    public ResponseEntity<List<MembershipPlan>> getMonthlyPlans() {
        List<MembershipPlan> plans = membershipService.getMonthlyPlans();
        return ResponseEntity.ok(plans);
    }

    @GetMapping("/plans/annual")
    public ResponseEntity<List<MembershipPlan>> getAnnualPlans() {
        List<MembershipPlan> plans = membershipService.getAnnualPlans();
        return ResponseEntity.ok(plans);
    }

    @GetMapping("/plans/{planId}")
    public ResponseEntity<MembershipPlan> getPlanById(@PathVariable Long planId) {
        Optional<MembershipPlan> plan = membershipService.getPlanById(planId);
        return plan.map(ResponseEntity::ok)
                  .orElse(ResponseEntity.notFound().build());
    }

    // Endpoints para membresías de usuario
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UserMembership>> getUserMemberships(@PathVariable Integer userId) {
        List<UserMembership> memberships = membershipService.getUserMemberships(userId);
        return ResponseEntity.ok(memberships);
    }

    @GetMapping("/user/{userId}/active")
    public ResponseEntity<UserMembership> getActiveMembership(@PathVariable Integer userId) {
        Optional<UserMembership> membership = membershipService.getActiveMembership(userId);
        return membership.map(ResponseEntity::ok)
                        .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}/status")
    public ResponseEntity<MembershipStatusResponse> getMembershipStatus(@PathVariable Integer userId) {
        boolean hasActive = membershipService.hasActiveMembership(userId);
        Optional<UserMembership> activeMembership = membershipService.getActiveMembership(userId);
        
        MembershipStatusResponse response = new MembershipStatusResponse();
        response.setHasActiveMembership(hasActive);
        response.setActiveMembership(activeMembership.orElse(null));
        
        return ResponseEntity.ok(response);
    }

    // Endpoints para pagos
    @GetMapping("/user/{userId}/payments")
    public ResponseEntity<List<MembershipPayment>> getUserPayments(@PathVariable Integer userId) {
        List<MembershipPayment> payments = membershipService.getUserPayments(userId);
        return ResponseEntity.ok(payments);
    }

    @PostMapping("/payments/{paymentId}/complete")
    public ResponseEntity<String> completePayment(@PathVariable Long paymentId, @RequestParam String transactionId) {
        try {
            membershipService.completePayment(paymentId, transactionId);
            return ResponseEntity.ok("Pago completado exitosamente");
        } catch (Exception e) {
            log.error("Error completing payment {}: {}", paymentId, e.getMessage());
            return ResponseEntity.badRequest().body("Error al completar el pago: " + e.getMessage());
        }
    }

    @PostMapping("/payments/{paymentId}/fail")
    public ResponseEntity<String> failPayment(@PathVariable Long paymentId, @RequestParam String reason) {
        try {
            membershipService.failPayment(paymentId, reason);
            return ResponseEntity.ok("Pago marcado como fallido");
        } catch (Exception e) {
            log.error("Error failing payment {}: {}", paymentId, e.getMessage());
            return ResponseEntity.badRequest().body("Error al marcar el pago como fallido: " + e.getMessage());
        }
    }

    // Endpoints para gestión de membresías
    @PostMapping("/memberships/{membershipId}/cancel")
    public ResponseEntity<String> cancelMembership(@PathVariable Long membershipId) {
        try {
            membershipService.cancelMembership(membershipId);
            return ResponseEntity.ok("Membresía cancelada exitosamente");
        } catch (Exception e) {
            log.error("Error cancelling membership {}: {}", membershipId, e.getMessage());
            return ResponseEntity.badRequest().body("Error al cancelar la membresía: " + e.getMessage());
        }
    }

    @PostMapping("/memberships/{membershipId}/renew")
    public ResponseEntity<UserMembership> renewMembership(@PathVariable Long membershipId) {
        try {
            UserMembership renewed = membershipService.renewMembership(membershipId);
            return ResponseEntity.ok(renewed);
        } catch (Exception e) {
            log.error("Error renewing membership {}: {}", membershipId, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    // Endpoint para verificar contratos con trainer
    @GetMapping("/user/{userId}/trainer/{trainerId}/contract")
    public ResponseEntity<Boolean> hasActiveContractWithTrainer(@PathVariable Integer userId, @PathVariable Integer trainerId) {
        boolean hasContract = membershipService.hasActiveContractWithTrainer(userId, trainerId);
        return ResponseEntity.ok(hasContract);
    }

    // Endpoint para estadísticas (admin)
    @GetMapping("/expiring")
    public ResponseEntity<List<UserMembership>> getExpiringMemberships(@RequestParam(defaultValue = "7") int daysAhead) {
        List<UserMembership> expiring = membershipService.getExpiringMemberships(daysAhead);
        return ResponseEntity.ok(expiring);
    }

    @PostMapping("/update-expired")
    public ResponseEntity<String> updateExpiredMemberships() {
        try {
            membershipService.updateExpiredMemberships();
            return ResponseEntity.ok("Membresías expiradas actualizadas");
        } catch (Exception e) {
            log.error("Error updating expired memberships: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Error al actualizar membresías expiradas");
        }
    }

    // Endpoints específicos para la página de suscripción del usuario
    @GetMapping("/user-subscription")
    public ResponseEntity<UserSubscriptionResponse> getCurrentUserSubscription() {
        Integer currentUserId = getCurrentUserId(); 
        
        if (currentUserId == null) {
            return ResponseEntity.badRequest().build();
        }
        
        Optional<UserMembership> membership = membershipService.getActiveMembership(currentUserId);
        if (membership.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        UserSubscriptionResponse response = createSubscriptionResponse(membership.get());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/payment-history")
    public ResponseEntity<List<MembershipPayment>> getCurrentUserPaymentHistory() {
        Integer currentUserId = getCurrentUserId();
        
        log.info("Getting payment history for user: {}", currentUserId);
        
        if (currentUserId == null) {
            log.warn("No current user ID found");
            return ResponseEntity.badRequest().build();
        }
        
        List<MembershipPayment> payments = membershipService.getUserPayments(currentUserId);
        log.info("Found {} payments for user {}", payments.size(), currentUserId);
        
        // Log each payment for debugging
        for (MembershipPayment payment : payments) {
            log.info("Payment: id={}, amount={}, status={}, processedAt={}", 
                payment.getId(), payment.getAmount(), payment.getStatus(), payment.getProcessedAt());
        }
        
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/debug/current-user")
    public ResponseEntity<Map<String, Object>> getCurrentUserDebug() {
        Integer currentUserId = getCurrentUserId();
        Map<String, Object> debug = new HashMap<>();
        debug.put("currentUserId", currentUserId);
        debug.put("authenticationPrincipal", SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        debug.put("authenticationName", SecurityContextHolder.getContext().getAuthentication().getName());
        return ResponseEntity.ok(debug);
    }

    // Endpoint para renovar/reactivar suscripción
    @PostMapping("/renew")
    public ResponseEntity<UserSubscriptionResponse> renewSubscription(@RequestBody Map<String, String> request) {
        Integer currentUserId = getCurrentUserId();
        
        if (currentUserId == null) {
            return ResponseEntity.badRequest().build();
        }
        
        try {
            String paymentMethod = request.getOrDefault("paymentMethod", "STRIPE");
            
            // Intentar reactivar la membresía expirada
            UserMembership membership = membershipService.reactivateExpiredMembership(currentUserId, paymentMethod);
            
            UserSubscriptionResponse response = createSubscriptionResponse(membership);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error renewing subscription for user {}: {}", currentUserId, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    // Clase para respuesta de estado de membresía
    public static class MembershipStatusResponse {
        private boolean hasActiveMembership;
        private UserMembership activeMembership;

        public boolean isHasActiveMembership() {
            return hasActiveMembership;
        }

        public void setHasActiveMembership(boolean hasActiveMembership) {
            this.hasActiveMembership = hasActiveMembership;
        }

        public UserMembership getActiveMembership() {
            return activeMembership;
        }

        public void setActiveMembership(UserMembership activeMembership) {
            this.activeMembership = activeMembership;
        }
    }

    // Método helper para crear la respuesta de suscripción
    private UserSubscriptionResponse createSubscriptionResponse(UserMembership membership) {
        UserSubscriptionResponse response = new UserSubscriptionResponse();
        response.setId(membership.getId());
        response.setMembershipType(membership.getMembershipType().toString());
        response.setStatus(membership.getStatus().toString());
        response.setStartDate(membership.getStartDate());
        response.setEndDate(membership.getEndDate());
        response.setAutoRenewal(membership.getAutoRenewal());
        
        // Información específica del plan de pago
        if (membership.getPlanId() != null) {
            // Obtener el plan desde el servicio
            Optional<MembershipPlan> plan = membershipService.getPlanById(membership.getPlanId());
            if (plan.isPresent()) {
                response.setPlanName(plan.get().getName());
            }
            
            // Información de pago
            if (membership.getPaymentAmount() != null) {
                response.setPaymentAmount(membership.getPaymentAmount().doubleValue());
            }
            response.setCurrency(membership.getCurrency());
            
            // Calcular próxima fecha de pago para membresías activas con renovación automática
            if (membership.getStatus() == UserMembership.MembershipStatus.ACTIVE && 
                membership.getAutoRenewal() && 
                membership.getEndDate() != null) {
                response.setNextPaymentDate(membership.getEndDate());
            } else if (membership.getStatus() == UserMembership.MembershipStatus.ACTIVE && 
                      membership.getEndDate() != null && 
                      !membership.getAutoRenewal()) {
                // Si no tiene renovación automática pero está activa, 
                // la próxima fecha de pago sería la fecha de finalización (si decide renovar)
                response.setNextPaymentDate(membership.getEndDate());
            }
        }
        
        // Información específica del contrato
        if (membership.getTrainerId() != null) {
            try {
                // Obtener información del trainer desde el servicio de usuarios
                // Por ahora usamos un placeholder ya que no tenemos acceso al TrainerService
                response.setTrainerName("Entrenador Personal");
                response.setContractDetails(membership.getContractDetails());
            } catch (Exception e) {
                log.warn("Could not load trainer information for membership {}: {}", membership.getId(), e.getMessage());
                response.setTrainerName("Entrenador Personal");
                response.setContractDetails(membership.getContractDetails());
            }
        }
        
        return response;
    }

    // Método para obtener el ID del usuario actual desde el contexto de seguridad
    private Integer getCurrentUserId() {
        try {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal instanceof UserDetails) {
                String username = ((UserDetails) principal).getUsername();
                User user = userService.getUserEntityByUsername(username);
                return user.getId();
            }
        } catch (Exception e) {
            log.error("Error getting current user ID: {}", e.getMessage());
        }
        return null;
    }

    // Clase para respuesta de suscripción del usuario
    public static class UserSubscriptionResponse {
        private Long id;
        private String membershipType;
        private String status;
        private java.time.LocalDate startDate;
        private java.time.LocalDate endDate;
        private Boolean autoRenewal;
        private String planName;
        private Double paymentAmount;
        private String currency;
        private java.time.LocalDate nextPaymentDate;
        private String trainerName;
        private String contractDetails;

        // Getters and setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getMembershipType() { return membershipType; }
        public void setMembershipType(String membershipType) { this.membershipType = membershipType; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public java.time.LocalDate getStartDate() { return startDate; }
        public void setStartDate(java.time.LocalDate startDate) { this.startDate = startDate; }

        public java.time.LocalDate getEndDate() { return endDate; }
        public void setEndDate(java.time.LocalDate endDate) { this.endDate = endDate; }

        public Boolean getAutoRenewal() { return autoRenewal; }
        public void setAutoRenewal(Boolean autoRenewal) { this.autoRenewal = autoRenewal; }

        public String getPlanName() { return planName; }
        public void setPlanName(String planName) { this.planName = planName; }

        public Double getPaymentAmount() { return paymentAmount; }
        public void setPaymentAmount(Double paymentAmount) { this.paymentAmount = paymentAmount; }

        public String getCurrency() { return currency; }
        public void setCurrency(String currency) { this.currency = currency; }

        public java.time.LocalDate getNextPaymentDate() { return nextPaymentDate; }
        public void setNextPaymentDate(java.time.LocalDate nextPaymentDate) { this.nextPaymentDate = nextPaymentDate; }

        public String getTrainerName() { return trainerName; }
        public void setTrainerName(String trainerName) { this.trainerName = trainerName; }

        public String getContractDetails() { return contractDetails; }
        public void setContractDetails(String contractDetails) { this.contractDetails = contractDetails; }
    }
} 