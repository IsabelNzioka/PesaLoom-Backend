package com.pesaloom.pesaloom.admin.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record AdminLoanDto(
        UUID id,
        UUID loanApplicationId,
        String referenceNumber,
        String applicantName,
        String loanType,
        BigDecimal principal,
        BigDecimal interestRate,
        int tenureMonths,
        BigDecimal emi,
        BigDecimal totalPayable,
        BigDecimal totalPaid,
        BigDecimal outstandingBalance,
        String status,
        Instant disbursedAt,
        Instant closedAt,
        List<LoanInstallmentDto> schedule,
        List<RepaymentDto> repayments
) {
}
