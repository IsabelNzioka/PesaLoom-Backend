package com.pesaloom.pesaloom.loan.entity;

import com.pesaloom.pesaloom.loanapplication.entity.LoanType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "loans")
public class Loan {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "loan_application_id", nullable = false)
    private UUID loanApplicationId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "reference_number", nullable = false)
    private String referenceNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "loan_type", nullable = false)
    private LoanType loanType;

    @Column(nullable = false)
    private BigDecimal principal;

    @Column(name = "interest_rate", nullable = false)
    private BigDecimal interestRate;

    @Column(name = "tenure_months", nullable = false)
    private int tenureMonths;

    @Column(nullable = false)
    private BigDecimal emi;

    @Column(name = "total_payable", nullable = false)
    private BigDecimal totalPayable;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoanStatus status;

    @Column(name = "disbursed_at", nullable = false)
    private Instant disbursedAt;

    @Column(name = "closed_at")
    private Instant closedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "branch_id")
    private UUID branchId;

    protected Loan() {
        // JPA
    }

    public Loan(UUID loanApplicationId, UUID userId, String referenceNumber, LoanType loanType,
                BigDecimal principal, BigDecimal interestRate, int tenureMonths, BigDecimal emi,
                BigDecimal totalPayable, Instant disbursedAt, UUID branchId) {
        this.loanApplicationId = loanApplicationId;
        this.userId = userId;
        this.referenceNumber = referenceNumber;
        this.loanType = loanType;
        this.principal = principal;
        this.interestRate = interestRate;
        this.tenureMonths = tenureMonths;
        this.emi = emi;
        this.totalPayable = totalPayable;
        this.status = LoanStatus.ACTIVE;
        this.disbursedAt = disbursedAt;
        this.createdAt = Instant.now();
        this.branchId = branchId;
    }

    public void close() {
        this.status = LoanStatus.CLOSED;
        this.closedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public UUID getLoanApplicationId() {
        return loanApplicationId;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public LoanType getLoanType() {
        return loanType;
    }

    public BigDecimal getPrincipal() {
        return principal;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public int getTenureMonths() {
        return tenureMonths;
    }

    public BigDecimal getEmi() {
        return emi;
    }

    public BigDecimal getTotalPayable() {
        return totalPayable;
    }

    public LoanStatus getStatus() {
        return status;
    }

    public Instant getDisbursedAt() {
        return disbursedAt;
    }

    public Instant getClosedAt() {
        return closedAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public UUID getBranchId() {
        return branchId;
    }
}
