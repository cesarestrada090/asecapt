package com.fitech.app.users.domain.services.impl;

import com.fitech.app.commons.util.MapperUtil;
import com.fitech.app.users.application.exception.UserNotFoundException;
import com.fitech.app.users.domain.entities.BankAccount;
import com.fitech.app.users.domain.entities.Person;
import com.fitech.app.users.application.dto.BankAccountDto;
import com.fitech.app.users.domain.services.BankAccountService;
import com.fitech.app.users.infrastructure.repository.BankAccountRepository;
import com.fitech.app.users.infrastructure.repository.PersonRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BankAccountServiceImpl implements BankAccountService {

    @Autowired
    private BankAccountRepository bankAccountRepository;

    @Autowired
    private PersonRepository personRepository;

    @Override
    public List<BankAccountDto> getBankAccountsByPersonId(Integer personId) {
        log.info("Getting bank accounts for person ID: {}", personId);
        
        // Verificar que la persona existe
        personRepository.findById(personId)
                .orElseThrow(() -> new UserNotFoundException("Person not found with id: " + personId));

        List<BankAccount> bankAccounts = bankAccountRepository.findByPersonIdOrderByIsPreferredDescCreatedAtDesc(personId);
        
        return bankAccounts.stream()
                .map(account -> MapperUtil.map(account, BankAccountDto.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BankAccountDto createBankAccount(BankAccountDto bankAccountDto) {
        log.info("Creating bank account for person ID: {}", bankAccountDto.getPersonId());
        
        // Verificar que la persona existe y es trainer
        Person person = personRepository.findById(bankAccountDto.getPersonId())
                .orElseThrow(() -> new UserNotFoundException("Person not found with id: " + bankAccountDto.getPersonId()));

        // Verificar que no existe una cuenta con el mismo número
        if (bankAccountRepository.existsByPersonIdAndAccountNumber(bankAccountDto.getPersonId(), bankAccountDto.getAccountNumber())) {
            throw new IllegalArgumentException("Account number already exists for this person");
        }

        // Si se marca como preferida o es la primera cuenta, remover preferencias anteriores
        if (bankAccountDto.getIsPreferred() || getBankAccountsByPersonId(bankAccountDto.getPersonId()).isEmpty()) {
            bankAccountRepository.removeAllPreferredByPersonId(bankAccountDto.getPersonId());
            bankAccountDto.setIsPreferred(true);
        }

        BankAccount bankAccount = MapperUtil.map(bankAccountDto, BankAccount.class);
        BankAccount savedAccount = bankAccountRepository.save(bankAccount);
        
        return MapperUtil.map(savedAccount, BankAccountDto.class);
    }

    @Override
    @Transactional
    public BankAccountDto updateBankAccount(Integer accountId, BankAccountDto bankAccountDto) {
        log.info("Updating bank account ID: {}", accountId);
        
        BankAccount existingAccount = bankAccountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Bank account not found with id: " + accountId));

        // Verificar que la cuenta pertenece a la persona
        if (!existingAccount.getPersonId().equals(bankAccountDto.getPersonId())) {
            throw new IllegalArgumentException("Bank account does not belong to person");
        }

        // Si se marca como preferida, remover preferencias anteriores
        if (bankAccountDto.getIsPreferred()) {
            bankAccountRepository.removeAllPreferredByPersonId(bankAccountDto.getPersonId());
        }

        // Actualizar campos
        existingAccount.setBankName(bankAccountDto.getBankName());
        existingAccount.setAccountNumber(bankAccountDto.getAccountNumber());
        existingAccount.setAccountType(bankAccountDto.getAccountType());
        existingAccount.setCurrency(bankAccountDto.getCurrency());
        existingAccount.setIsPreferred(bankAccountDto.getIsPreferred());
        existingAccount.setAccountHolderName(bankAccountDto.getAccountHolderName());
        existingAccount.setSwiftCode(bankAccountDto.getSwiftCode());

        BankAccount updatedAccount = bankAccountRepository.save(existingAccount);
        
        return MapperUtil.map(updatedAccount, BankAccountDto.class);
    }

    @Override
    @Transactional
    public void deleteBankAccount(Integer accountId) {
        log.info("Deleting bank account ID: {}", accountId);
        
        BankAccount bankAccount = bankAccountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Bank account not found with id: " + accountId));

        bankAccountRepository.delete(bankAccount);
    }

    @Override
    @Transactional
    public void setPreferredAccount(Integer personId, Integer accountId) {
        log.info("Setting preferred account ID: {} for person ID: {}", accountId, personId);
        
        // Verificar que la cuenta existe y pertenece a la persona
        BankAccount bankAccount = bankAccountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Bank account not found with id: " + accountId));

        if (!bankAccount.getPersonId().equals(personId)) {
            throw new IllegalArgumentException("Bank account does not belong to person");
        }

        // Remover preferencias anteriores y establecer la nueva
        bankAccountRepository.removeAllPreferredByPersonId(personId);
        bankAccount.setIsPreferred(true);
        bankAccountRepository.save(bankAccount);
    }
} 