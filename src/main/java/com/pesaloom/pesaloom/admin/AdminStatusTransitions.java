package com.pesaloom.pesaloom.admin;

import com.pesaloom.pesaloom.exception.InvalidStatusTransitionException;
import com.pesaloom.pesaloom.loanapplication.entity.LoanApplicationStatus;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;


public final class AdminStatusTransitions {

    private static final Map<LoanApplicationStatus, Set<LoanApplicationStatus>> LEGAL = new EnumMap<>(LoanApplicationStatus.class);

    static {
        LEGAL.put(LoanApplicationStatus.SUBMITTED, EnumSet.of(LoanApplicationStatus.UNDER_REVIEW));
        LEGAL.put(LoanApplicationStatus.UNDER_REVIEW, EnumSet.of(LoanApplicationStatus.APPROVED, LoanApplicationStatus.REJECTED));
        LEGAL.put(LoanApplicationStatus.APPROVED, EnumSet.of(LoanApplicationStatus.DISBURSED));
        LEGAL.put(LoanApplicationStatus.REJECTED, EnumSet.noneOf(LoanApplicationStatus.class));
        LEGAL.put(LoanApplicationStatus.DISBURSED, EnumSet.noneOf(LoanApplicationStatus.class));
        LEGAL.put(LoanApplicationStatus.DRAFT, EnumSet.noneOf(LoanApplicationStatus.class));
    }

    public static void requireLegal(LoanApplicationStatus from, LoanApplicationStatus to) {
        Set<LoanApplicationStatus> allowed = LEGAL.getOrDefault(from, Set.of());
        if (!allowed.contains(to)) {
            throw new InvalidStatusTransitionException(
                    "Cannot move an application from " + from + " to " + to);
        }
    }

    private AdminStatusTransitions() {
    }
}
