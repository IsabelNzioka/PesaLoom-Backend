package com.pesaloom.pesaloom.admin.dto;

import com.pesaloom.pesaloom.loanapplication.dto.LoanApplicationDto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;


public record AdminLoanApplicationDto(
        LoanApplicationDto application,
        UUID userId,
        String userEmail,
        boolean createdByStaff,
        UUID reviewedBy,
        Instant reviewedAt,
        String reviewNotes,
        Instant disbursedAt,
        UUID assignedTo,
        String description,
        List<UUID> staffAccessUserIds,
        UUID loanId
) {
}
