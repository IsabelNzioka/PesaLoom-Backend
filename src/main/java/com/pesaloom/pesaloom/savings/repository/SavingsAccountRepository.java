package com.pesaloom.pesaloom.savings.repository;

import com.pesaloom.pesaloom.savings.entity.SavingsAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SavingsAccountRepository extends JpaRepository<SavingsAccount, UUID> {

    boolean existsByAccountNumber(String accountNumber);
}
