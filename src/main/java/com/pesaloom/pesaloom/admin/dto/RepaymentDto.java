package com.pesaloom.pesaloom.admin.dto;

import com.pesaloom.pesaloom.loan.entity.Repayment;
import com.pesaloom.pesaloom.loan.entity.RepaymentMethod;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record RepaymentDto(
        UUID id,
        BigDecimal amount,
        RepaymentMethod method,
        Instant paidAt,
        String notes
) {
    public static RepaymentDto from(Repayment r) {
        return new RepaymentDto(r.getId(), r.getAmount(), r.getMethod(), r.getPaidAt(), r.getNotes());
    }
}
