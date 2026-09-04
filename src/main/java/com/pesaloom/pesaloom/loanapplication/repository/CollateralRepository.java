package com.pesaloom.pesaloom.loanapplication.repository;

import com.pesaloom.pesaloom.loanapplication.entity.Collateral;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CollateralRepository extends JpaRepository<Collateral, UUID> {

    List<Collateral> findByLoanApplicationIdOrderByRegisteredAtDesc(UUID loanApplicationId);
}
