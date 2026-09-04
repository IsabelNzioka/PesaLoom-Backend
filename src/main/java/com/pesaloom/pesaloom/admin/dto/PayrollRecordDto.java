package com.pesaloom.pesaloom.admin.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PayrollRecordDto(
        UUID id,
        UUID staffUserId,
        String staffName,
        String periodMonth,
        BigDecimal baseSalary,
        BigDecimal deductions,
        BigDecimal netPay,
        Instant paidAt,
        String notes
) {
}
