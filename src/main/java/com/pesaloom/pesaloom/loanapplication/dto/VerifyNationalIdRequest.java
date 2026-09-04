package com.pesaloom.pesaloom.loanapplication.dto;

import jakarta.validation.constraints.NotBlank;

public record VerifyNationalIdRequest(
        @NotBlank(message = "National ID is required")
        String nationalId
) {
}
