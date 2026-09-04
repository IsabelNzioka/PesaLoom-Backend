package com.pesaloom.pesaloom.loan;

import com.pesaloom.pesaloom.loanapplication.EmiCalculator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;


public final class LoanAmortization {

    public record Installment(
            int number,
            LocalDate dueDate,
            BigDecimal principalDue,
            BigDecimal interestDue,
            BigDecimal totalDue
    ) {
    }

    public record Schedule(
            BigDecimal emi,
            BigDecimal interestRate,
            BigDecimal totalPrincipal,
            BigDecimal totalInterest,
            BigDecimal totalPayable,
            BigDecimal processingFee,
            List<Installment> installments
    ) {
    }

    public static Schedule schedule(BigDecimal principal, BigDecimal annualRatePercent, int tenureMonths, Instant startingFrom) {
        EmiCalculator.Breakdown breakdown = EmiCalculator.calculate(principal, annualRatePercent, tenureMonths);
        BigDecimal monthlyRate = annualRatePercent
                .divide(BigDecimal.valueOf(12), 10, RoundingMode.HALF_UP)
                .divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP);

        LocalDate firstDueDate = startingFrom.atZone(ZoneOffset.UTC).toLocalDate().plusMonths(1);

        List<Installment> installments = new ArrayList<>(tenureMonths);
        BigDecimal openingBalance = principal;
        BigDecimal totalInterest = BigDecimal.ZERO;

        for (int number = 1; number <= tenureMonths; number++) {
            LocalDate dueDate = firstDueDate.plusMonths(number - 1L);
            BigDecimal interestDue = openingBalance.multiply(monthlyRate).setScale(0, RoundingMode.HALF_UP);
            BigDecimal principalDue;

            if (number == tenureMonths) {

                principalDue = openingBalance;
            } else {
                principalDue = breakdown.emi().subtract(interestDue);
                if (principalDue.compareTo(openingBalance) > 0) {
                    principalDue = openingBalance;
                }
            }

            BigDecimal totalDue = principalDue.add(interestDue);
            installments.add(new Installment(number, dueDate, principalDue, interestDue, totalDue));

            openingBalance = openingBalance.subtract(principalDue);
            totalInterest = totalInterest.add(interestDue);
        }

        BigDecimal totalPayable = principal.add(totalInterest);

        return new Schedule(
                breakdown.emi(),
                annualRatePercent,
                principal,
                totalInterest,
                totalPayable,
                breakdown.processingFee(),
                installments
        );
    }

    private LoanAmortization() {
    }
}
