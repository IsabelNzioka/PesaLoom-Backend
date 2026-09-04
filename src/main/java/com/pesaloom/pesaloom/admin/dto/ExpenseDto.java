package com.pesaloom.pesaloom.admin.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ExpenseDto(
        UUID id,
        String category,
        String description,
        BigDecimal amount,
        Instant incurredAt,
        String recordedByName,
        String notes
) {
}
