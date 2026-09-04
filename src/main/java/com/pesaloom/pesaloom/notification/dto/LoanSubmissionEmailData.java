package com.pesaloom.pesaloom.notification.dto;

import java.math.BigDecimal;
import java.time.Instant;


public record LoanSubmissionEmailData(
        String recipientEmail,
        String recipientFirstName,
        String referenceNumber,
        String loanType,
        BigDecimal loanAmount,
        int tenureMonths,
        BigDecimal emi,
        BigDecimal interestRate,
        BigDecimal processingFee,
        BigDecimal totalInterest,
        BigDecimal totalPayable,
        Instant submittedAt
) {
}
