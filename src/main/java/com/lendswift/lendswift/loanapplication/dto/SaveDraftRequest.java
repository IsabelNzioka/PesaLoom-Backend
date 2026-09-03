package com.lendswift.lendswift.loanapplication.dto;

import com.lendswift.lendswift.loanapplication.entity.AddressInfo;
import com.lendswift.lendswift.loanapplication.entity.CoApplicantInfo;
import com.lendswift.lendswift.loanapplication.entity.Consents;
import com.lendswift.lendswift.loanapplication.entity.DisbursementInfo;
import com.lendswift.lendswift.loanapplication.entity.EmploymentInfo;
import com.lendswift.lendswift.loanapplication.entity.PersonalInfo;

import java.math.BigDecimal;


public record SaveDraftRequest(
        String loanType,
        BigDecimal loanAmount,
        Integer loanTenureMonths,
        String loanPurpose,
        String referralCode,
        Integer currentStep,
        String kraPin,
        String nationalId,
        Boolean idConsent,
        PersonalInfo personalInfo,
        AddressInfo address,
        EmploymentInfo employment,
        CoApplicantInfo coApplicant,
        DisbursementInfo disbursement,
        Consents consents
) {
}
