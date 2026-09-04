package com.pesaloom.pesaloom.admin.dto;

import com.pesaloom.pesaloom.loan.LoanAmortization;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record LoanCalculatorResponseDto(
        BigDecimal emi,
        BigDecimal interestRate,
        BigDecimal totalPrincipal,
        BigDecimal totalInterest,
        BigDecimal totalPayable,
        BigDecimal processingFee,
        List<PreviewInstallment> schedule
) {
    public record PreviewInstallment(int number, LocalDate dueDate, BigDecimal principalDue,
                                      BigDecimal interestDue, BigDecimal totalDue) {
        public static PreviewInstallment from(LoanAmortization.Installment i) {
            return new PreviewInstallment(i.number(), i.dueDate(), i.principalDue(), i.interestDue(), i.totalDue());
        }
    }

    public static LoanCalculatorResponseDto from(LoanAmortization.Schedule schedule) {
        return new LoanCalculatorResponseDto(
                schedule.emi(), schedule.interestRate(), schedule.totalPrincipal(), schedule.totalInterest(),
                schedule.totalPayable(), schedule.processingFee(),
                schedule.installments().stream().map(PreviewInstallment::from).toList()
        );
    }
}
