package com.pesaloom.pesaloom.admin.dto;

import com.pesaloom.pesaloom.loan.entity.RepaymentMethod;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record AdminRepaymentDto(
        UUID id,
        Instant paidAt,
        BigDecimal amount,
        RepaymentMethod method,
        UUID loanId,
        String loanReferenceNumber,
        String applicantName,
        String recordedByName,
        String notes
) {
}
