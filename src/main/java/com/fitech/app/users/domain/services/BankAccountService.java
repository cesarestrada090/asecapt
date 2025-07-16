package com.fitech.app.users.domain.services;

import com.fitech.app.users.application.dto.BankAccountDto;

import java.util.List;

public interface BankAccountService {
    List<BankAccountDto> getBankAccountsByPersonId(Integer personId);
    BankAccountDto createBankAccount(BankAccountDto bankAccountDto);
    BankAccountDto updateBankAccount(Integer accountId, BankAccountDto bankAccountDto);
    void deleteBankAccount(Integer accountId);
    void setPreferredAccount(Integer personId, Integer accountId);
} 