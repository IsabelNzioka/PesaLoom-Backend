package com.pesaloom.pesaloom.admin.dto;

import com.pesaloom.pesaloom.loan.entity.RepaymentMethod;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.Instant;

public record RecordRepaymentRequest(
        @NotNull(message = "Amount is required")
        @Positive(message = "Amount must be greater than zero")
        BigDecimal amount,

        @NotNull(message = "Payment method is required")
        RepaymentMethod method,

        @NotNull(message = "Payment date is required")
        Instant paidAt,

        String notes
) {
}
