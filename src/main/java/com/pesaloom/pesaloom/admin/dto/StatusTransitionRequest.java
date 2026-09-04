package com.pesaloom.pesaloom.admin.dto;

import com.pesaloom.pesaloom.loanapplication.entity.LoanApplicationStatus;
import jakarta.validation.constraints.NotNull;

public record StatusTransitionRequest(
        @NotNull(message = "Target status is required")
        LoanApplicationStatus targetStatus,
        String reviewNotes
) {
}
