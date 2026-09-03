package com.lendswift.lendswift.loanapplication.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record SubmitResponse(
        String referenceNumber,
        BigDecimal emi,
        BigDecimal interestRate,
        BigDecimal processingFee,
        BigDecimal totalInterest,
        BigDecimal totalPayable,
        Instant submittedAt
) {
}
