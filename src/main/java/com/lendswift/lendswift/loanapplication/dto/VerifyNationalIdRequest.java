package com.lendswift.lendswift.loanapplication.dto;

import jakarta.validation.constraints.NotBlank;

public record VerifyNationalIdRequest(
        @NotBlank(message = "National ID is required")
        String nationalId
) {
}
