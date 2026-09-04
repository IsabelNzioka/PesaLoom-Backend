package com.pesaloom.pesaloom.admin.dto;

import com.pesaloom.pesaloom.loanapplication.entity.CollateralType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record AddCollateralRequest(
        @NotNull(message = "Collateral type is required")
        CollateralType type,

        @NotBlank(message = "Description is required")
        String description,

        @NotNull(message = "Estimated value is required")
        @Positive(message = "Estimated value must be greater than zero")
        BigDecimal estimatedValue
) {
}
