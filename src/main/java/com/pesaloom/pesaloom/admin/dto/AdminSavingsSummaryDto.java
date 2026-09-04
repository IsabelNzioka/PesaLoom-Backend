package com.pesaloom.pesaloom.admin.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record AdminSavingsSummaryDto(
        UUID id,
        String accountNumber,
        String ownerName,
        BigDecimal balance,
        String status,
        Instant openedAt
) {
}
