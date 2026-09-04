package com.pesaloom.pesaloom.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.Instant;

public record RecordExpenseRequest(
        @NotBlank(message = "Category is required")
        String category,

        @NotBlank(message = "Description is required")
        String description,

        @NotNull(message = "Amount is required")
        @Positive(message = "Amount must be greater than zero")
        BigDecimal amount,

        @NotNull(message = "Date is required")
        Instant incurredAt,

        String notes
) {
}
