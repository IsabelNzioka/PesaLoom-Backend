package com.lendswift.lendswift.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ResetPasswordRequest(

        @NotBlank(message = "Reset token is required")
        String token,

        @NotBlank(message = "Password is required")
        @Size(min = 10, max = 72, message = "Password must be between 10 and 72 characters")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).+$",
                message = "Password must include an uppercase letter, a lowercase letter, a number, and a symbol"
        )
        String newPassword
) {
}
