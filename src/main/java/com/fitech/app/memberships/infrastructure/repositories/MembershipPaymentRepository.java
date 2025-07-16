package com.fitech.app.memberships.infrastructure.repositories;

import com.fitech.app.memberships.domain.entities.MembershipPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MembershipPaymentRepository extends JpaRepository<MembershipPayment, Long> {

    // Find payments by membership
    List<MembershipPayment> findByMembershipIdOrderByCreatedAtDesc(Long membershipId);

    // Find payments by user
    List<MembershipPayment> findByUserIdOrderByCreatedAtDesc(Integer userId);

    // Find payments by service contract
    List<MembershipPayment> findByServiceContractId(Integer serviceContractId);

    // Find payments by service contract and collection status
    List<MembershipPayment> findByServiceContractIdAndCollectionStatus(Integer serviceContractId, String collectionStatus);

    // Find payments by status
    List<MembershipPayment> findByStatusOrderByCreatedAtDesc(String status);

    // Find payments by status and created before
    List<MembershipPayment> findByStatusAndCreatedAtBefore(String status, LocalDateTime cutoff);

    // Find payment by transaction ID
    Optional<MembershipPayment> findByTransactionId(String transactionId);

    // Calculate total paid by user (only collected payments)
    @Query("SELECT COALESCE(SUM(mp.amount), 0) FROM MembershipPayment mp WHERE mp.userId = :userId AND mp.status = 'COLLECTED'")
    BigDecimal getTotalPaidByUser(@Param("userId") Integer userId);

    // Calculate total paid by membership (only collected payments)
    @Query("SELECT COALESCE(SUM(mp.amount), 0) FROM MembershipPayment mp WHERE mp.membershipId = :membershipId AND mp.status = 'COLLECTED'")
    BigDecimal getTotalPaidByMembership(@Param("membershipId") Long membershipId);

    // Find last successful payment by user
    @Query("SELECT mp FROM MembershipPayment mp WHERE mp.userId = :userId AND mp.status = 'COLLECTED' ORDER BY mp.processedAt DESC LIMIT 1")
    Optional<MembershipPayment> findLastSuccessfulPaymentByUser(@Param("userId") Integer userId);

    // Find payments between dates
    @Query("SELECT mp FROM MembershipPayment mp WHERE mp.createdAt BETWEEN :startDate AND :endDate ORDER BY mp.createdAt DESC")
    List<MembershipPayment> findPaymentsBetweenDates(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    // Payment statistics by status
    @Query("SELECT mp.status, COUNT(mp), COALESCE(SUM(mp.amount), 0) FROM MembershipPayment mp GROUP BY mp.status")
    List<Object[]> getPaymentStatistics();

    // Find payments with specific status since date
    @Query("SELECT mp FROM MembershipPayment mp WHERE mp.status = :status AND mp.createdAt > :since ORDER BY mp.createdAt DESC")
    List<MembershipPayment> findPaymentsByStatusSince(@Param("status") String status, @Param("since") LocalDateTime since);

    // Check if membership has collected payment
    @Query("SELECT COUNT(mp) > 0 FROM MembershipPayment mp WHERE mp.membershipId = :membershipId AND mp.status = 'COLLECTED'")
    boolean hasCompletedPayment(@Param("membershipId") Long membershipId);

    // Get revenue by payment method (only collected payments)
    @Query("SELECT mp.paymentMethod, COALESCE(SUM(mp.amount), 0) FROM MembershipPayment mp WHERE mp.status = 'COLLECTED' GROUP BY mp.paymentMethod")
    List<Object[]> getRevenueByPaymentMethod();

    // Find pending payments for trainer collection
    @Query("SELECT mp FROM MembershipPayment mp WHERE mp.status = 'PENDING' ORDER BY mp.createdAt ASC")
    List<MembershipPayment> findPendingPayments();

    // Find observed payments that need review
    @Query("SELECT mp FROM MembershipPayment mp WHERE mp.status = 'OBSERVED' ORDER BY mp.createdAt DESC")
    List<MembershipPayment> findObservedPayments();
} 