package com.pesaloom.pesaloom.admin.dto;

import com.pesaloom.pesaloom.loanapplication.entity.Collateral;
import com.pesaloom.pesaloom.loanapplication.entity.CollateralType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record CollateralDto(
        UUID id,
        UUID loanApplicationId,
        String referenceNumber,
        String applicantName,
        CollateralType type,
        String description,
        BigDecimal estimatedValue,
        Instant registeredAt
) {
    public static CollateralDto from(Collateral c, String referenceNumber, String applicantName) {
        return new CollateralDto(
                c.getId(), c.getLoanApplicationId(), referenceNumber, applicantName,
                c.getType(), c.getDescription(), c.getEstimatedValue(), c.getRegisteredAt()
        );
    }
}
