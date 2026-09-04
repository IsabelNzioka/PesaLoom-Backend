package com.pesaloom.pesaloom.admin.dto;

import com.pesaloom.pesaloom.loanapplication.entity.LoanApplication;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record AdminLoanApplicationSummaryDto(
        UUID id,
        String referenceNumber,
        String applicantName,
        String businessName,
        String loanType,
        BigDecimal loanAmount,
        String status,
        boolean createdByStaff,
        Instant submittedAt,
        Instant updatedAt
) {
    public static AdminLoanApplicationSummaryDto from(LoanApplication a) {
        String applicantName = a.getPersonalInfo() != null ? a.getPersonalInfo().fullName() : null;
        String businessName = a.getEmployment() != null ? a.getEmployment().businessName() : null;
        return new AdminLoanApplicationSummaryDto(
                a.getId(),
                a.getReferenceNumber(),
                applicantName != null ? applicantName : businessName,
                businessName,
                a.getLoanType() != null ? a.getLoanType().name() : null,
                a.getLoanAmount(),
                a.getStatus().name(),
                a.isCreatedByStaff(),
                a.getSubmittedAt(),
                a.getUpdatedAt()
        );
    }
}
