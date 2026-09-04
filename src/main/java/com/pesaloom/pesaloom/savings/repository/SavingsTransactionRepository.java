package com.pesaloom.pesaloom.savings.repository;

import com.pesaloom.pesaloom.savings.entity.SavingsTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SavingsTransactionRepository extends JpaRepository<SavingsTransaction, UUID> {

    List<SavingsTransaction> findByAccountIdOrderByCreatedAtDesc(UUID accountId);
}
