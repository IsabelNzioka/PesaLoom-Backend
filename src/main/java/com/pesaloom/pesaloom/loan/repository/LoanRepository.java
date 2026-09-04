package com.pesaloom.pesaloom.loan.repository;

import com.pesaloom.pesaloom.loan.entity.Loan;
import com.pesaloom.pesaloom.loan.entity.LoanStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LoanRepository extends JpaRepository<Loan, UUID> {

    Optional<Loan> findByLoanApplicationId(UUID loanApplicationId);

    List<Loan> findByStatus(LoanStatus status);

    List<Loan> findByUserId(UUID userId);
}
