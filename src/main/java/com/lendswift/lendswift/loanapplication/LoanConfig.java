package com.lendswift.lendswift.loanapplication;

import com.lendswift.lendswift.loanapplication.entity.LoanType;

import java.math.BigDecimal;
import java.util.Map;


public final class LoanConfig {

    public record Config(
            BigDecimal minAmount,
            BigDecimal maxAmount,
            int minTenureMonths,
            int maxTenureMonths,
            BigDecimal interestRate,
            BigDecimal coApplicantThreshold,
            boolean coApplicantAlwaysRequired,
            BigDecimal passportRequiredAboveAmount
    ) {
    }

    private static final Map<LoanType, Config> CONFIG = Map.of(
            LoanType.PERSONAL, new Config(
                    BigDecimal.valueOf(5_000), BigDecimal.valueOf(3_000_000),
                    12, 60, BigDecimal.valueOf(15.0),
                    BigDecimal.valueOf(1_500_000), false, null
            ),
            LoanType.HOME, new Config(
                    BigDecimal.valueOf(500_000), BigDecimal.valueOf(50_000_000),
                    60, 360, BigDecimal.valueOf(13.0),
                    null, true, BigDecimal.valueOf(25_000_000)
            ),
            LoanType.BUSINESS, new Config(
                    BigDecimal.valueOf(50_000), BigDecimal.valueOf(20_000_000),
                    12, 120, BigDecimal.valueOf(17.0),
                    BigDecimal.valueOf(10_000_000), false, null
            )
    );

    public static Config of(LoanType loanType) {
        Config config = CONFIG.get(loanType);
        if (config == null) {
            throw new IllegalArgumentException("Unknown loan type: " + loanType);
        }
        return config;
    }

    public static boolean isCoApplicantRequired(LoanType loanType, BigDecimal amount) {
        Config config = of(loanType);
        if (config.coApplicantAlwaysRequired()) return true;
        return config.coApplicantThreshold() != null && amount != null
                && amount.compareTo(config.coApplicantThreshold()) > 0;
    }

    public static boolean isPassportRequired(LoanType loanType, BigDecimal amount) {
        Config config = of(loanType);
        return config.passportRequiredAboveAmount() != null && amount != null
                && amount.compareTo(config.passportRequiredAboveAmount()) > 0;
    }

    private LoanConfig() {
    }
}
