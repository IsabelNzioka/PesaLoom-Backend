package com.pesaloom.pesaloom.admin.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record AdminBorrowerSummaryDto(
        UUID userId,
        String name,
        String email,
        long applicationsCount,
        long activeLoansCount,
        BigDecimal totalOutstanding,
        Instant joinedAt
) {
}
