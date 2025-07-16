package com.fitech.app.users.infrastructure.repository;

import com.fitech.app.users.domain.entities.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount, Integer> {
    
    List<BankAccount> findByPersonIdOrderByIsPreferredDescCreatedAtDesc(Integer personId);
    
    @Query("SELECT ba FROM BankAccount ba WHERE ba.personId = :personId AND ba.isPreferred = true")
    BankAccount findPreferredAccountByPersonId(@Param("personId") Integer personId);
    
    @Modifying
    @Query("UPDATE BankAccount ba SET ba.isPreferred = false WHERE ba.personId = :personId")
    void removeAllPreferredByPersonId(@Param("personId") Integer personId);
    
    boolean existsByPersonIdAndAccountNumber(Integer personId, String accountNumber);
} 