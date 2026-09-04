package com.pesaloom.pesaloom.admin.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public record AdminDashboardSummaryDto(
        Map<String, Long> countsByStatus,
        BigDecimal totalRequestedAmount,
        BigDecimal totalDisbursedAmount,
        BigDecimal averageLoanAmount,
        List<MonthlyPoint> monthlyTrend,
        Map<String, Long> loanTypeBreakdown,
        Map<String, Long> genderBreakdown,
        long registeredBorrowers,
        long awaitingReviewCount,
        Double approvalRatePercent,
        Double averageDecisionDays
) {
    public record MonthlyPoint(String month, long count) {
    }
}
