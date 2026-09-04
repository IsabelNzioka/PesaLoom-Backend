package com.pesaloom.pesaloom.loanapplication;

import com.pesaloom.pesaloom.loanapplication.entity.LoanType;

import java.util.LinkedHashSet;
import java.util.Set;


public final class RequiredDocuments {

    public static Set<String> forApplication(LoanType loanType, String employmentType) {
        Set<String> required = new LinkedHashSet<>(Set.of(
                "kraPinCertificate", "nationalId", "photograph", "bankStatements", "signature"
        ));

        if ("SALARIED".equals(employmentType)) {
            required.add("salarySlips");
        } else if ("SELF_EMPLOYED".equals(employmentType) || "BUSINESS_OWNER".equals(employmentType)) {
            required.add("itr");
        }

        if (loanType == LoanType.HOME) {
            required.add("propertyDocs");
        } else if (loanType == LoanType.BUSINESS) {
            required.add("businessRegistration");
            required.add("kraTaxComplianceCertificate");
        }

        return required;
    }

    private RequiredDocuments() {
    }
}
