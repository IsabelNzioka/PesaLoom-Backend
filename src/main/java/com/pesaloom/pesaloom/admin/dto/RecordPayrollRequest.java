package com.pesaloom.pesaloom.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.util.UUID;

public record RecordPayrollRequest(
        @NotNull(message = "Staff member is required")
        UUID staffUserId,

        @NotBlank(message = "Period is required")
        String periodMonth,

        @NotNull(message = "Base salary is required")
        @Positive(message = "Base salary must be greater than zero")
        BigDecimal baseSalary,

        @PositiveOrZero(message = "Deductions cannot be negative")
        BigDecimal deductions,

        String notes
) {
}
