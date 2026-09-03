package com.lendswift.lendswift.loanapplication.entity;

public record CoApplicantInfo(
        String coApplicantName,
        String coApplicantRelationship,
        String coApplicantKraPin,
        Double coApplicantIncome,
        Boolean coApplicantConsent
) {
}
