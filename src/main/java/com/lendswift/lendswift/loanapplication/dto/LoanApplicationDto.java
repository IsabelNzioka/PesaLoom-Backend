package com.lendswift.lendswift.loanapplication.dto;

import com.lendswift.lendswift.loanapplication.entity.AddressInfo;
import com.lendswift.lendswift.loanapplication.entity.CoApplicantInfo;
import com.lendswift.lendswift.loanapplication.entity.Consents;
import com.lendswift.lendswift.loanapplication.entity.DisbursementInfo;
import com.lendswift.lendswift.loanapplication.entity.EmploymentInfo;
import com.lendswift.lendswift.loanapplication.entity.PersonalInfo;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record LoanApplicationDto(
        UUID id,
        String status,
        String loanType,
        BigDecimal loanAmount,
        Integer loanTenureMonths,
        String loanPurpose,
        String referralCode,
        String referenceNumber,
        int currentStep,
        String kraPin,
        boolean kraPinVerified,
        String nationalId,
        boolean nationalIdVerified,
        boolean idConsent,
        PersonalInfo personalInfo,
        AddressInfo address,
        EmploymentInfo employment,
        CoApplicantInfo coApplicant,
        DisbursementInfo disbursement,
        Consents consents,
        BigDecimal emi,
        BigDecimal interestRate,
        BigDecimal processingFee,
        BigDecimal totalInterest,
        BigDecimal totalPayable,
        Instant submittedAt,
        Instant updatedAt,
        List<DocumentDto> documents
) {
}
