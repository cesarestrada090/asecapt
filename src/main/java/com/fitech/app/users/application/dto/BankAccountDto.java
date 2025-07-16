package com.fitech.app.users.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;
import java.time.LocalDateTime;

@Schema(description = "DTO for bank account information management")
public class BankAccountDto implements Serializable {
    @Schema(description = "Bank account ID", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer id;
    
    @NotNull(message = "Person ID is required")
    @Schema(description = "ID of the person who owns the account", example = "123", required = true)
    private Integer personId;
    
    @NotBlank(message = "Bank name is required")
    @Size(max = 100, message = "Bank name must be less than 100 characters")
    @Schema(description = "Name of the bank", example = "Banco de Crédito del Perú", maxLength = 100, required = true)
    private String bankName;
    
    @NotBlank(message = "Account number is required")
    @Size(max = 50, message = "Account number must be less than 50 characters")
    @Pattern(regexp = "^[0-9-]+$", message = "Account number must contain only numbers and hyphens")
    @Schema(description = "Bank account number", example = "123-456789-012", maxLength = 50, pattern = "^[0-9-]+$", required = true)
    private String accountNumber;
    
    @NotBlank(message = "Account type is required")
    @Size(max = 20, message = "Account type must be less than 20 characters")
    @Schema(description = "Type of bank account", example = "SAVINGS", allowableValues = {"SAVINGS", "CHECKING", "BUSINESS"}, maxLength = 20, required = true)
    private String accountType;
    
    @NotBlank(message = "Currency is required")
    @Size(max = 10, message = "Currency must be less than 10 characters")
    @Schema(description = "Account currency", example = "PEN", allowableValues = {"PEN", "USD", "EUR"}, maxLength = 10, required = true)
    private String currency;
    
    @NotNull(message = "isPreferred is required")
    @Schema(description = "Whether this is the preferred account for payments", example = "true", required = true)
    private Boolean isPreferred = false;
    
    @NotBlank(message = "Account holder name is required")
    @Size(max = 100, message = "Account holder name must be less than 100 characters")
    @Schema(description = "Full name of the account holder", example = "Juan Carlos Pérez López", maxLength = 100, required = true)
    private String accountHolderName;
    
    @Size(max = 20, message = "SWIFT code must be less than 20 characters")
    @Schema(description = "SWIFT/BIC code for international transfers", example = "BCPLPEPL", maxLength = 20)
    private String swiftCode;
    
    @Schema(description = "Account creation timestamp", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createdAt;
    
    @Schema(description = "Last update timestamp", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime updatedAt;

    // Constructors
    public BankAccountDto() {}

    public BankAccountDto(Integer id, Integer personId, String bankName, String accountNumber, 
                         String accountType, String currency, Boolean isPreferred, 
                         String accountHolderName, String swiftCode) {
        this.id = id;
        this.personId = personId;
        this.bankName = bankName;
        this.accountNumber = accountNumber;
        this.accountType = accountType;
        this.currency = currency;
        this.isPreferred = isPreferred;
        this.accountHolderName = accountHolderName;
        this.swiftCode = swiftCode;
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPersonId() {
        return personId;
    }

    public void setPersonId(Integer personId) {
        this.personId = personId;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Boolean getIsPreferred() {
        return isPreferred;
    }

    public void setIsPreferred(Boolean isPreferred) {
        this.isPreferred = isPreferred;
    }

    public String getAccountHolderName() {
        return accountHolderName;
    }

    public void setAccountHolderName(String accountHolderName) {
        this.accountHolderName = accountHolderName;
    }

    public String getSwiftCode() {
        return swiftCode;
    }

    public void setSwiftCode(String swiftCode) {
        this.swiftCode = swiftCode;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
} 