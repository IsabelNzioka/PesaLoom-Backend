package com.pesaloom.pesaloom.admin.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record AdminBorrowerDetailDto(
        UUID userId,
        String name,
        String email,
        Instant joinedAt,
        List<AdminLoanApplicationSummaryDto> applications,
        List<AdminLoanSummaryDto> loans
) {
}
