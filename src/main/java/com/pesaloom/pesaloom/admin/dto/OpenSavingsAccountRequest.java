package com.pesaloom.pesaloom.admin.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.util.UUID;

public record OpenSavingsAccountRequest(
        @NotNull(message = "Borrower is required")
        UUID userId,

        @NotNull(message = "Opening deposit is required")
        @PositiveOrZero(message = "Opening deposit cannot be negative")
        BigDecimal openingDeposit,

        @NotNull(message = "Interest rate is required")
        @PositiveOrZero(message = "Interest rate cannot be negative")
        BigDecimal interestRate
) {
}
