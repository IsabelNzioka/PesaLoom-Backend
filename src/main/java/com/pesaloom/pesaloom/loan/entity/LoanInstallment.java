package com.pesaloom.pesaloom.loan.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "loan_installments")
public class LoanInstallment {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "loan_id", nullable = false)
    private UUID loanId;

    @Column(name = "installment_number", nullable = false)
    private int installmentNumber;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(name = "principal_due", nullable = false)
    private BigDecimal principalDue;

    @Column(name = "interest_due", nullable = false)
    private BigDecimal interestDue;

    @Column(name = "total_due", nullable = false)
    private BigDecimal totalDue;

    @Column(name = "amount_paid", nullable = false)
    private BigDecimal amountPaid = BigDecimal.ZERO;

    protected LoanInstallment() {
        // JPA
    }

    public LoanInstallment(UUID loanId, int installmentNumber, LocalDate dueDate,
                            BigDecimal principalDue, BigDecimal interestDue, BigDecimal totalDue) {
        this.loanId = loanId;
        this.installmentNumber = installmentNumber;
        this.dueDate = dueDate;
        this.principalDue = principalDue;
        this.interestDue = interestDue;
        this.totalDue = totalDue;
        this.amountPaid = BigDecimal.ZERO;
    }

    public BigDecimal balance() {
        return totalDue.subtract(amountPaid);
    }

    public boolean isFullyPaid() {
        return balance().signum() <= 0;
    }


    public BigDecimal applyPayment(BigDecimal available) {
        BigDecimal balance = balance();
        if (balance.signum() <= 0 || available.signum() <= 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal applied = available.min(balance);
        this.amountPaid = this.amountPaid.add(applied);
        return applied;
    }

    public UUID getId() {
        return id;
    }

    public UUID getLoanId() {
        return loanId;
    }

    public int getInstallmentNumber() {
        return installmentNumber;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public BigDecimal getPrincipalDue() {
        return principalDue;
    }

    public BigDecimal getInterestDue() {
        return interestDue;
    }

    public BigDecimal getTotalDue() {
        return totalDue;
    }

    public BigDecimal getAmountPaid() {
        return amountPaid;
    }
}
