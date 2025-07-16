package com.fitech.app.trainers.application.dto;

import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO for paginated payments response with summary")
public class PaymentsResponseDTO {
    @Schema(description = "List of payment transactions")
    private List<PaymentDataDTO> payments;
    
    @Schema(description = "Payment summary totals")
    private PaymentSummaryDTO summary;
    
    @Schema(description = "Total number of pages", example = "5")
    private Integer totalPages;
    
    @Schema(description = "Current page number", example = "1")
    private Integer currentPage;
    
    @Schema(description = "Total number of payment records", example = "47")
    private Long totalElements;

    public PaymentsResponseDTO() {}

    public PaymentsResponseDTO(List<PaymentDataDTO> payments, PaymentSummaryDTO summary,
                             Integer totalPages, Integer currentPage, Long totalElements) {
        this.payments = payments;
        this.summary = summary;
        this.totalPages = totalPages;
        this.currentPage = currentPage;
        this.totalElements = totalElements;
    }

    public List<PaymentDataDTO> getPayments() { return payments; }
    public void setPayments(List<PaymentDataDTO> payments) { this.payments = payments; }

    public PaymentSummaryDTO getSummary() { return summary; }
    public void setSummary(PaymentSummaryDTO summary) { this.summary = summary; }

    public Integer getTotalPages() { return totalPages; }
    public void setTotalPages(Integer totalPages) { this.totalPages = totalPages; }

    public Integer getCurrentPage() { return currentPage; }
    public void setCurrentPage(Integer currentPage) { this.currentPage = currentPage; }

    public Long getTotalElements() { return totalElements; }
    public void setTotalElements(Long totalElements) { this.totalElements = totalElements; }
} 