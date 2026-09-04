package com.pesaloom.pesaloom.expense.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "expenses")
public class Expense {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(name = "incurred_at", nullable = false)
    private Instant incurredAt;

    @Column(name = "recorded_by", nullable = false)
    private UUID recordedBy;

    @Column
    private String notes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected Expense() {
        // JPA
    }

    public Expense(String category, String description, BigDecimal amount, Instant incurredAt, UUID recordedBy, String notes) {
        this.category = category;
        this.description = description;
        this.amount = amount;
        this.incurredAt = incurredAt;
        this.recordedBy = recordedBy;
        this.notes = notes;
        this.createdAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Instant getIncurredAt() {
        return incurredAt;
    }

    public UUID getRecordedBy() {
        return recordedBy;
    }

    public String getNotes() {
        return notes;
    }
}
