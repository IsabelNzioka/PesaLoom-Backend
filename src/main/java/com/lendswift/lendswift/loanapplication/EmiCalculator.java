package com.lendswift.lendswift.loanapplication;

import java.math.BigDecimal;
import java.math.RoundingMode;


public final class EmiCalculator {

    private static final BigDecimal PROCESSING_FEE_RATE = BigDecimal.valueOf(0.01);
    private static final BigDecimal PROCESSING_FEE_MIN = BigDecimal.valueOf(1_000);
    private static final BigDecimal PROCESSING_FEE_MAX = BigDecimal.valueOf(50_000);

    public record Breakdown(
            BigDecimal emi,
            BigDecimal interestRate,
            BigDecimal totalPayable,
            BigDecimal totalInterest,
            BigDecimal processingFee
    ) {
    }

    public static Breakdown calculate(BigDecimal principal, BigDecimal annualRatePercent, int tenureMonths) {
        BigDecimal emi = calculateEmi(principal, annualRatePercent, tenureMonths);
        BigDecimal totalPayable = emi.multiply(BigDecimal.valueOf(tenureMonths));
        BigDecimal totalInterest = totalPayable.subtract(principal);
        BigDecimal processingFee = calculateProcessingFee(principal);

        return new Breakdown(
                emi.setScale(0, RoundingMode.HALF_UP),
                annualRatePercent,
                totalPayable.setScale(0, RoundingMode.HALF_UP),
                totalInterest.setScale(0, RoundingMode.HALF_UP),
                processingFee.setScale(0, RoundingMode.HALF_UP)
        );
    }

    private static BigDecimal calculateEmi(BigDecimal principal, BigDecimal annualRatePercent, int months) {
        if (principal == null || principal.signum() == 0 || months <= 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal monthlyRate = annualRatePercent
                .divide(BigDecimal.valueOf(12), 10, RoundingMode.HALF_UP)
                .divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP);

        if (monthlyRate.signum() == 0) {
            return principal.divide(BigDecimal.valueOf(months), 10, RoundingMode.HALF_UP);
        }

        BigDecimal factor = BigDecimal.ONE.add(monthlyRate).pow(months);
        BigDecimal numerator = principal.multiply(monthlyRate).multiply(factor);
        BigDecimal denominator = factor.subtract(BigDecimal.ONE);
        return numerator.divide(denominator, 10, RoundingMode.HALF_UP);
    }

    private static BigDecimal calculateProcessingFee(BigDecimal principal) {
        BigDecimal fee = principal.multiply(PROCESSING_FEE_RATE);
        return fee.max(PROCESSING_FEE_MIN).min(PROCESSING_FEE_MAX);
    }

    private EmiCalculator() {
    }
}
