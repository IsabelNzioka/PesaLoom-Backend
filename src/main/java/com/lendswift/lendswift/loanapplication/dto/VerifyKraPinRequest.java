package com.lendswift.lendswift.loanapplication.dto;

import jakarta.validation.constraints.NotBlank;

public record VerifyKraPinRequest(
        @NotBlank(message = "KRA PIN is required")
        String kraPin
) {
}
