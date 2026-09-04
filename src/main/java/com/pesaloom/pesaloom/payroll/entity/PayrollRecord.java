package com.pesaloom.pesaloom.payroll.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "payroll_records")
public class PayrollRecord {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "staff_user_id", nullable = false)
    private UUID staffUserId;

    @Column(name = "period_month", nullable = false)
    private String periodMonth;

    @Column(name = "base_salary", nullable = false)
    private BigDecimal baseSalary;

    @Column(nullable = false)
    private BigDecimal deductions;

    @Column(name = "net_pay", nullable = false)
    private BigDecimal netPay;

    @Column(name = "paid_at", nullable = false)
    private Instant paidAt;

    @Column(name = "recorded_by", nullable = false)
    private UUID recordedBy;

    @Column
    private String notes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected PayrollRecord() {
        // JPA
    }

    public PayrollRecord(UUID staffUserId, String periodMonth, BigDecimal baseSalary, BigDecimal deductions,
                          UUID recordedBy, String notes) {
        this.staffUserId = staffUserId;
        this.periodMonth = periodMonth;
        this.baseSalary = baseSalary;
        this.deductions = deductions;
        this.netPay = baseSalary.subtract(deductions);
        this.paidAt = Instant.now();
        this.recordedBy = recordedBy;
        this.notes = notes;
        this.createdAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public UUID getStaffUserId() {
        return staffUserId;
    }

    public String getPeriodMonth() {
        return periodMonth;
    }

    public BigDecimal getBaseSalary() {
        return baseSalary;
    }

    public BigDecimal getDeductions() {
        return deductions;
    }

    public BigDecimal getNetPay() {
        return netPay;
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
}
