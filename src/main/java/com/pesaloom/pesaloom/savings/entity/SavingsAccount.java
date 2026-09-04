package com.pesaloom.pesaloom.savings.entity;

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
@Table(name = "savings_accounts")
public class SavingsAccount {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "account_number", nullable = false)
    private String accountNumber;

    @Column(nullable = false)
    private BigDecimal balance;

    @Column(name = "interest_rate", nullable = false)
    private BigDecimal interestRate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SavingsStatus status;

    @Column(name = "opened_at", nullable = false)
    private Instant openedAt;

    @Column(name = "closed_at")
    private Instant closedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected SavingsAccount() {
        // JPA
    }

    public SavingsAccount(UUID userId, String accountNumber, BigDecimal openingDeposit, BigDecimal interestRate) {
        this.userId = userId;
        this.accountNumber = accountNumber;
        this.balance = openingDeposit;
        this.interestRate = interestRate;
        this.status = SavingsStatus.ACTIVE;
        this.openedAt = Instant.now();
        this.createdAt = Instant.now();
    }

    public void applyDeposit(BigDecimal amount) {
        this.balance = this.balance.add(amount);
    }

    public void applyWithdrawal(BigDecimal amount) {
        this.balance = this.balance.subtract(amount);
    }

    public void close() {
        this.status = SavingsStatus.CLOSED;
        this.closedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public SavingsStatus getStatus() {
        return status;
    }

    public Instant getOpenedAt() {
        return openedAt;
    }

    public Instant getClosedAt() {
        return closedAt;
    }
}
