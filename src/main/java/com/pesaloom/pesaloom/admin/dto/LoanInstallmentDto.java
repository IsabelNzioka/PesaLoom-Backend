package com.pesaloom.pesaloom.admin.dto;

import com.pesaloom.pesaloom.loan.entity.LoanInstallment;

import java.math.BigDecimal;
import java.time.LocalDate;

public record LoanInstallmentDto(
        int number,
        LocalDate dueDate,
        BigDecimal principalDue,
        BigDecimal interestDue,
        BigDecimal totalDue,
        BigDecimal amountPaid,
        BigDecimal balance,
        boolean overdue
) {
    public static LoanInstallmentDto from(LoanInstallment i, LocalDate today) {
        BigDecimal balance = i.balance();
        boolean overdue = balance.signum() > 0 && i.getDueDate().isBefore(today);
        return new LoanInstallmentDto(
                i.getInstallmentNumber(), i.getDueDate(), i.getPrincipalDue(), i.getInterestDue(),
                i.getTotalDue(), i.getAmountPaid(), balance, overdue
        );
    }
}
