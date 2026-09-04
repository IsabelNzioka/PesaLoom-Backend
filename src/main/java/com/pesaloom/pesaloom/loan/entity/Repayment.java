package com.pesaloom.pesaloom.loan.entity;

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
@Table(name = "loan_repayments")
public class Repayment {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "loan_id", nullable = false)
    private UUID loanId;

    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RepaymentMethod method;

    @Column(name = "paid_at", nullable = false)
    private Instant paidAt;

    @Column(name = "recorded_by", nullable = false)
    private UUID recordedBy;

    @Column
    private String notes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected Repayment() {
        // JPA
    }

    public Repayment(UUID loanId, BigDecimal amount, RepaymentMethod method, Instant paidAt,
                      UUID recordedBy, String notes) {
        this.loanId = loanId;
        this.amount = amount;
        this.method = method;
        this.paidAt = paidAt;
        this.recordedBy = recordedBy;
        this.notes = notes;
        this.createdAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public UUID getLoanId() {
        return loanId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public RepaymentMethod getMethod() {
        return method;
    }

    public Instant getPaidAt() {
        return paidAt;
    }

    public UUID getRecordedBy() {
        return recordedBy;
    }

    public String getNotes() {
        return notes;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
