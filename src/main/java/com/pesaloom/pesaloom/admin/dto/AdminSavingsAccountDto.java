package com.pesaloom.pesaloom.admin.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record AdminSavingsAccountDto(
        UUID id,
        UUID userId,
        String accountNumber,
        String ownerName,
        BigDecimal balance,
        BigDecimal interestRate,
        String status,
        Instant openedAt,
        Instant closedAt,
        List<SavingsTransactionDto> transactions
) {
}
