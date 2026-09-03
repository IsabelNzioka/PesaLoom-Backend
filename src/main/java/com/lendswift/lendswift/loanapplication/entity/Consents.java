package com.lendswift.lendswift.loanapplication.entity;

public record Consents(
        Boolean consentAccurate,
        Boolean consentCreditCheck,
        Boolean consentTerms,
        Boolean consentCommunications
) {
}
