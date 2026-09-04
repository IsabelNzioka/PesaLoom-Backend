package com.pesaloom.pesaloom.loanapplication.dto;

import com.pesaloom.pesaloom.loanapplication.entity.AddressInfo;
import com.pesaloom.pesaloom.loanapplication.entity.CoApplicantInfo;
import com.pesaloom.pesaloom.loanapplication.entity.Consents;
import com.pesaloom.pesaloom.loanapplication.entity.DisbursementInfo;
import com.pesaloom.pesaloom.loanapplication.entity.EmploymentInfo;
import com.pesaloom.pesaloom.loanapplication.entity.PersonalInfo;

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
