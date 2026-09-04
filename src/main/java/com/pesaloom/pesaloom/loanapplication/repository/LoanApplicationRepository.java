package com.pesaloom.pesaloom.loanapplication.repository;

import com.pesaloom.pesaloom.loanapplication.entity.LoanApplication;
import com.pesaloom.pesaloom.loanapplication.entity.LoanApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LoanApplicationRepository extends JpaRepository<LoanApplication, UUID> {

    Optional<LoanApplication> findByUserIdAndStatus(UUID userId, LoanApplicationStatus status);

    Optional<LoanApplication> findByIdAndUserId(UUID id, UUID userId);

    List<LoanApplication> findAllByUserIdOrderByCreatedAtDesc(UUID userId);
}
