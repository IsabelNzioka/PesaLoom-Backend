package com.pesaloom.pesaloom.admin.dto;

import com.pesaloom.pesaloom.savings.entity.TransactionType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record SavingsTransactionDto(
        UUID id,
        TransactionType type,
        BigDecimal amount,
        BigDecimal balanceAfter,
        String recordedByName,
        String notes,
        Instant createdAt
) {
}
