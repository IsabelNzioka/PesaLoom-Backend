package com.pesaloom.pesaloom.admin.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record AdminLoanSummaryDto(
        UUID id,
        String referenceNumber,
        String applicantName,
        String loanType,
        BigDecimal principal,
        BigDecimal emi,
        BigDecimal outstandingBalance,
        LocalDate nextDueDate,
        String status,
        boolean overdue,
        long daysOverdue
) {
}
