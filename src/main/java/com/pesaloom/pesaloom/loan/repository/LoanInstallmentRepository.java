package com.pesaloom.pesaloom.loan.repository;

import com.pesaloom.pesaloom.loan.entity.LoanInstallment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface LoanInstallmentRepository extends JpaRepository<LoanInstallment, UUID> {

    List<LoanInstallment> findByLoanIdOrderByInstallmentNumberAsc(UUID loanId);

    List<LoanInstallment> findByLoanIdInOrderByDueDateAsc(Collection<UUID> loanIds);
}
