package com.pesaloom.pesaloom.loanapplication.entity;

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
@Table(name = "collateral")
public class Collateral {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "loan_application_id", nullable = false)
    private UUID loanApplicationId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CollateralType type;

    @Column(nullable = false)
    private String description;

    @Column(name = "estimated_value", nullable = false)
    private BigDecimal estimatedValue;

    @Column(name = "registered_by", nullable = false)
    private UUID registeredBy;

    @Column(name = "registered_at", nullable = false)
    private Instant registeredAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected Collateral() {
        // JPA
    }

    public Collateral(UUID loanApplicationId, CollateralType type, String description,
                       BigDecimal estimatedValue, UUID registeredBy) {
        this.loanApplicationId = loanApplicationId;
        this.type = type;
        this.description = description;
        this.estimatedValue = estimatedValue;
        this.registeredBy = registeredBy;
        this.registeredAt = Instant.now();
        this.createdAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public UUID getLoanApplicationId() {
        return loanApplicationId;
    }

    public CollateralType getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getEstimatedValue() {
        return estimatedValue;
    }

    public UUID getRegisteredBy() {
        return registeredBy;
    }

    public Instant getRegisteredAt() {
        return registeredAt;
    }
}
