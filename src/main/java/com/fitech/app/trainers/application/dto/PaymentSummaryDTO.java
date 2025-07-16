package com.fitech.app.trainers.application.dto;

import java.math.BigDecimal;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO for trainer payment summary and earnings overview")
public class PaymentSummaryDTO {
    @Schema(description = "Total amount collected to date", example = "2500.00")
    private BigDecimal collectedToDate;
    
    @Schema(description = "Amount pending collection", example = "150.00")
    private BigDecimal pendingCollection;
    
    @Schema(description = "Amount available for collection", example = "300.00")
    private BigDecimal availableForCollection;

    public PaymentSummaryDTO() {}

    public PaymentSummaryDTO(BigDecimal collectedToDate, BigDecimal pendingCollection, BigDecimal availableForCollection) {
        this.collectedToDate = collectedToDate;
        this.pendingCollection = pendingCollection;
        this.availableForCollection = availableForCollection;
    }

    public BigDecimal getCollectedToDate() { 
        return collectedToDate; 
    }
    
    public void setCollectedToDate(BigDecimal collectedToDate) { 
        this.collectedToDate = collectedToDate; 
    }

    public BigDecimal getPendingCollection() { 
        return pendingCollection; 
    }
    
    public void setPendingCollection(BigDecimal pendingCollection) { 
        this.pendingCollection = pendingCollection; 
    }

    public BigDecimal getAvailableForCollection() { 
        return availableForCollection; 
    }
    
    public void setAvailableForCollection(BigDecimal availableForCollection) { 
        this.availableForCollection = availableForCollection; 
    }
} 