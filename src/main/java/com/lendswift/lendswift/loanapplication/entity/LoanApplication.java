package com.lendswift.lendswift.loanapplication.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "loan_applications")
public class LoanApplication {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoanApplicationStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "loan_type")
    private LoanType loanType;

    @Column(name = "loan_amount")
    private BigDecimal loanAmount;

    @Column(name = "loan_tenure_months")
    private Integer loanTenureMonths;

    @Column(name = "loan_purpose")
    private String loanPurpose;

    @Column(name = "referral_code")
    private String referralCode;

    @Column(name = "reference_number")
    private String referenceNumber;

    @Column(name = "current_step", nullable = false)
    private int currentStep = 1;

    @Column(name = "kra_pin")
    private String kraPin;

    @Column(name = "kra_pin_verified", nullable = false)
    private boolean kraPinVerified = false;

    @Column(name = "kra_pin_verified_at")
    private Instant kraPinVerifiedAt;

    @Column(name = "national_id")
    private String nationalId;

    @Column(name = "national_id_verified", nullable = false)
    private boolean nationalIdVerified = false;

    @Column(name = "national_id_verified_at")
    private Instant nationalIdVerifiedAt;

    @Column(name = "id_consent", nullable = false)
    private boolean idConsent = false;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "personal_info", columnDefinition = "jsonb")
    private PersonalInfo personalInfo;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private AddressInfo address;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private EmploymentInfo employment;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "co_applicant", columnDefinition = "jsonb")
    private CoApplicantInfo coApplicant;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private DisbursementInfo disbursement;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Consents consents;

    @Column
    private BigDecimal emi;

    @Column(name = "interest_rate")
    private BigDecimal interestRate;

    @Column(name = "processing_fee")
    private BigDecimal processingFee;

    @Column(name = "total_interest")
    private BigDecimal totalInterest;

    @Column(name = "total_payable")
    private BigDecimal totalPayable;

    @Column(name = "submitted_at")
    private Instant submittedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    protected LoanApplication() {
        // JPA
    }

    public LoanApplication(UUID userId) {
        this.userId = userId;
        this.status = LoanApplicationStatus.DRAFT;
        this.createdAt = Instant.now();
    }

    public void touch() {
        this.updatedAt = Instant.now();
    }

    public void markSubmitted(String referenceNumber, BigDecimal emi, BigDecimal interestRate,
                               BigDecimal processingFee, BigDecimal totalInterest, BigDecimal totalPayable) {
        this.status = LoanApplicationStatus.SUBMITTED;
        this.referenceNumber = referenceNumber;
        this.emi = emi;
        this.interestRate = interestRate;
        this.processingFee = processingFee;
        this.totalInterest = totalInterest;
        this.totalPayable = totalPayable;
        this.submittedAt = Instant.now();
        touch();
    }

    public boolean isDraft() {
        return status == LoanApplicationStatus.DRAFT;
    }

    // Getters

    public UUID getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    public LoanApplicationStatus getStatus() {
        return status;
    }

    public LoanType getLoanType() {
        return loanType;
    }

    public BigDecimal getLoanAmount() {
        return loanAmount;
    }

    public Integer getLoanTenureMonths() {
        return loanTenureMonths;
    }

    public String getLoanPurpose() {
        return loanPurpose;
    }

    public String getReferralCode() {
        return referralCode;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public int getCurrentStep() {
        return currentStep;
    }

    public String getKraPin() {
        return kraPin;
    }

    public boolean isKraPinVerified() {
        return kraPinVerified;
    }

    public Instant getKraPinVerifiedAt() {
        return kraPinVerifiedAt;
    }

    public String getNationalId() {
        return nationalId;
    }

    public boolean isNationalIdVerified() {
        return nationalIdVerified;
    }

    public Instant getNationalIdVerifiedAt() {
        return nationalIdVerifiedAt;
    }

    public boolean isIdConsent() {
        return idConsent;
    }

    public PersonalInfo getPersonalInfo() {
        return personalInfo;
    }

    public AddressInfo getAddress() {
        return address;
    }

    public EmploymentInfo getEmployment() {
        return employment;
    }

    public CoApplicantInfo getCoApplicant() {
        return coApplicant;
    }

    public DisbursementInfo getDisbursement() {
        return disbursement;
    }

    public Consents getConsents() {
        return consents;
    }

    public BigDecimal getEmi() {
        return emi;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public BigDecimal getProcessingFee() {
        return processingFee;
    }

    public BigDecimal getTotalInterest() {
        return totalInterest;
    }

    public BigDecimal getTotalPayable() {
        return totalPayable;
    }

    public Instant getSubmittedAt() {
        return submittedAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }



    public void setLoanType(LoanType loanType) {
        this.loanType = loanType;
    }

    public void setLoanAmount(BigDecimal loanAmount) {
        this.loanAmount = loanAmount;
    }

    public void setLoanTenureMonths(Integer loanTenureMonths) {
        this.loanTenureMonths = loanTenureMonths;
    }

    public void setLoanPurpose(String loanPurpose) {
        this.loanPurpose = loanPurpose;
    }

    public void setReferralCode(String referralCode) {
        this.referralCode = referralCode;
    }

    public void setCurrentStep(int currentStep) {
        this.currentStep = currentStep;
    }

    public void setKraPin(String kraPin) {
        this.kraPin = kraPin;
    }

    public void setKraPinVerified(boolean kraPinVerified, Instant verifiedAt) {
        this.kraPinVerified = kraPinVerified;
        this.kraPinVerifiedAt = verifiedAt;
    }

    public void setNationalId(String nationalId) {
        this.nationalId = nationalId;
    }

    public void setNationalIdVerified(boolean nationalIdVerified, Instant verifiedAt) {
        this.nationalIdVerified = nationalIdVerified;
        this.nationalIdVerifiedAt = verifiedAt;
    }

    public void setIdConsent(boolean idConsent) {
        this.idConsent = idConsent;
    }

    public void setPersonalInfo(PersonalInfo personalInfo) {
        this.personalInfo = personalInfo;
    }

    public void setAddress(AddressInfo address) {
        this.address = address;
    }

    public void setEmployment(EmploymentInfo employment) {
        this.employment = employment;
    }

    public void setCoApplicant(CoApplicantInfo coApplicant) {
        this.coApplicant = coApplicant;
    }

    public void setDisbursement(DisbursementInfo disbursement) {
        this.disbursement = disbursement;
    }

    public void setConsents(Consents consents) {
        this.consents = consents;
    }
}
