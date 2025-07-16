package com.fitech.app.trainers.infrastructure.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class TrainerPaymentsRepository {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Find trainer payments with client information (paginated)
     */
    public Page<Object[]> findTrainerPaymentsWithClientInfo(
            Integer trainerId, String status, LocalDateTime startDate, 
            LocalDateTime endDate, Pageable pageable) {
        
        String baseQuery = """
            SELECT 
                mp.id,
                mp.user_id,
                CONCAT(p.first_name, ' ', p.last_name) as client_name,
                COALESCE(mp.description, 'Pago de Servicio') as service_name,
                mp.amount,
                mp.processed_at as payment_date,
                mp.status as payment_status,
                mp.collection_status,
                mp.payment_method,
                mp.transaction_id,
                mp.collected_at,
                mp.collection_requested_at
            FROM membership_payments mp
            INNER JOIN user_memberships um ON mp.membership_id = um.id
            INNER JOIN user u ON mp.user_id = u.id
            INNER JOIN person p ON u.person_id = p.id
            WHERE um.trainer_id = :trainerId
            AND um.membership_type = 'CONTRACT'
            AND mp.status = 'COMPLETED'
            """;
        
        String whereClause = "";
        if (status != null) {
            whereClause += " AND mp.collection_status = :status";
        }
        if (startDate != null) {
            whereClause += " AND mp.processed_at >= :startDate";
        }
        if (endDate != null) {
            whereClause += " AND mp.processed_at <= :endDate";
        }
        
        String orderClause = " ORDER BY mp.processed_at DESC";
        
        // Count query
        String countQuery = """
            SELECT COUNT(*)
            FROM membership_payments mp
            INNER JOIN user_memberships um ON mp.membership_id = um.id
            WHERE um.trainer_id = :trainerId
            AND um.membership_type = 'CONTRACT'
            """ + whereClause;
        
        // Create queries
        Query dataQuery = entityManager.createNativeQuery(baseQuery + whereClause + orderClause);
        Query totalQuery = entityManager.createNativeQuery(countQuery);
        
        // Set parameters
        dataQuery.setParameter("trainerId", trainerId);
        totalQuery.setParameter("trainerId", trainerId);
        
        if (status != null) {
            dataQuery.setParameter("status", status);
            totalQuery.setParameter("status", status);
        }
        if (startDate != null) {
            dataQuery.setParameter("startDate", startDate);
            totalQuery.setParameter("startDate", startDate);
        }
        if (endDate != null) {
            dataQuery.setParameter("endDate", endDate);
            totalQuery.setParameter("endDate", endDate);
        }
        
        // Apply pagination
        dataQuery.setFirstResult((int) pageable.getOffset());
        dataQuery.setMaxResults(pageable.getPageSize());
        
        // Execute queries
        @SuppressWarnings("unchecked")
        List<Object[]> results = dataQuery.getResultList();
        Long total = ((Number) totalQuery.getSingleResult()).longValue();
        
        return new PageImpl<>(results, pageable, total);
    }

    /**
     * Get trainer payment summary (collected to date and available for collection)
     */
    public Object[] getTrainerPaymentSummary(Integer trainerId) {
        String queryStr = """
            SELECT 
                COALESCE(SUM(CASE WHEN mp.collection_status = 'COLLECTED' THEN (mp.amount * 0.95) ELSE 0 END), 0) as collected_to_date,
                COALESCE(SUM(CASE WHEN mp.collection_status IN ('PENDING_CLIENT_APPROVAL', 'PROCESSING_COLLECTION') OR mp.collection_status IS NULL THEN (mp.amount * 0.95) ELSE 0 END), 0) as pending_collection,
                COALESCE(SUM(CASE WHEN mp.collection_status = 'AVAILABLE_FOR_COLLECTION' THEN (mp.amount * 0.95) ELSE 0 END), 0) as available_for_collection
            FROM membership_payments mp
            INNER JOIN user_memberships um ON mp.membership_id = um.id
            WHERE um.trainer_id = :trainerId
            AND um.membership_type = 'CONTRACT'
            AND mp.status = 'COMPLETED'
            """;
        
        Query query = entityManager.createNativeQuery(queryStr);
        query.setParameter("trainerId", trainerId);
        
        return (Object[]) query.getSingleResult();
    }

    /**
     * Get trainer monthly earnings (no longer needed - removing)
     */
    public Object[] getTrainerMonthlyEarnings(Integer trainerId, LocalDateTime monthStart, LocalDateTime monthEnd) {
        // This method is no longer needed but keeping for compatibility
        return new Object[]{BigDecimal.ZERO};
    }

    /**
     * Get count of pending payments for trainer (no longer needed - included in summary)
     */
    public Integer getTrainerPendingPaymentsCount(Integer trainerId) {
        // This method is no longer needed but keeping for compatibility
        return 0;
    }

    /**
     * Find trainer payments for export (no pagination)
     */
    public List<Object[]> findTrainerPaymentsForExport(Integer trainerId, LocalDateTime startDate, LocalDateTime endDate) {
        String queryStr = """
            SELECT 
                mp.id,
                mp.user_id,
                CONCAT(p.first_name, ' ', p.last_name) as client_name,
                COALESCE(mp.description, 'Pago de Servicio') as service_name,
                mp.amount,
                mp.processed_at as payment_date,
                mp.status as payment_status,
                mp.collection_status,
                mp.payment_method,
                mp.transaction_id,
                mp.collected_at,
                mp.collection_requested_at
            FROM membership_payments mp
            INNER JOIN user_memberships um ON mp.membership_id = um.id
            INNER JOIN user u ON mp.user_id = u.id
            INNER JOIN person p ON u.person_id = p.id
            WHERE um.trainer_id = :trainerId
            AND um.membership_type = 'CONTRACT'
            AND mp.status = 'COMPLETED'
            """;
        
        String whereClause = "";
        if (startDate != null) {
            whereClause += " AND mp.processed_at >= :startDate";
        }
        if (endDate != null) {
            whereClause += " AND mp.processed_at <= :endDate";
        }
        
        String orderClause = " ORDER BY mp.processed_at DESC";
        
        Query query = entityManager.createNativeQuery(queryStr + whereClause + orderClause);
        query.setParameter("trainerId", trainerId);
        
        if (startDate != null) {
            query.setParameter("startDate", startDate);
        }
        if (endDate != null) {
            query.setParameter("endDate", endDate);
        }
        
        @SuppressWarnings("unchecked")
        List<Object[]> results = query.getResultList();
        return results;
    }
} 