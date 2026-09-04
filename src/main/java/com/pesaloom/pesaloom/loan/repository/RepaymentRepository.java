package com.pesaloom.pesaloom.loan.repository;

import com.pesaloom.pesaloom.loan.entity.Repayment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RepaymentRepository extends JpaRepository<Repayment, UUID> {

    List<Repayment> findByLoanIdOrderByPaidAtDesc(UUID loanId);
}
