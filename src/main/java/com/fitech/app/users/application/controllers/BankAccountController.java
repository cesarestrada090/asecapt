package com.fitech.app.users.application.controllers;

import com.fitech.app.users.application.dto.BankAccountDto;
import com.fitech.app.users.domain.services.BankAccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/v1/app/bank-accounts")
@Tag(name = "Bank Accounts", description = "Bank account information management for payments")
@SecurityRequirement(name = "bearerAuth")
public class BankAccountController {

    @Autowired
    private BankAccountService bankAccountService;

    @GetMapping("/person/{personId}")
    public ResponseEntity<List<BankAccountDto>> getBankAccountsByPerson(@PathVariable("personId") Integer personId) {
        List<BankAccountDto> bankAccounts = bankAccountService.getBankAccountsByPersonId(personId);
        return ResponseEntity.ok(bankAccounts);
    }

    @PostMapping
    public ResponseEntity<BankAccountDto> createBankAccount(@Valid @RequestBody BankAccountDto bankAccountDto) {
        BankAccountDto createdAccount = bankAccountService.createBankAccount(bankAccountDto);
        return ResponseEntity.ok(createdAccount);
    }

    @PutMapping("/{accountId}")
    public ResponseEntity<BankAccountDto> updateBankAccount(
            @PathVariable("accountId") Integer accountId,
            @Valid @RequestBody BankAccountDto bankAccountDto) {
        BankAccountDto updatedAccount = bankAccountService.updateBankAccount(accountId, bankAccountDto);
        return ResponseEntity.ok(updatedAccount);
    }

    @DeleteMapping("/{accountId}")
    public ResponseEntity<Void> deleteBankAccount(@PathVariable("accountId") Integer accountId) {
        bankAccountService.deleteBankAccount(accountId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/person/{personId}/preferred/{accountId}")
    public ResponseEntity<Void> setPreferredAccount(
            @PathVariable("personId") Integer personId,
            @PathVariable("accountId") Integer accountId) {
        bankAccountService.setPreferredAccount(personId, accountId);
        return ResponseEntity.noContent().build();
    }
} 