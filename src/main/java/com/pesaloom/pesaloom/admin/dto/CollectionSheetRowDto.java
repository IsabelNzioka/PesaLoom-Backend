package com.pesaloom.pesaloom.admin.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record CollectionSheetRowDto(
        UUID loanId,
        String referenceNumber,
        String applicantName,
        int installmentNumber,
        LocalDate dueDate,
        BigDecimal totalDue,
        BigDecimal balance
) {
}
