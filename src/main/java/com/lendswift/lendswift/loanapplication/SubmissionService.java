package com.lendswift.lendswift.loanapplication;

import com.lendswift.lendswift.exception.InvalidApplicationStateException;
import com.lendswift.lendswift.exception.SubmissionValidationException;
import com.lendswift.lendswift.loanapplication.dto.SubmitResponse;
import com.lendswift.lendswift.loanapplication.entity.LoanApplication;
import com.lendswift.lendswift.loanapplication.repository.DocumentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SubmissionService {

    private static final String REFERENCE_ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"; // no 0/O/1/I
    private static final SecureRandom RANDOM = new SecureRandom();

    private final LoanApplicationService loanApplicationService;
    private final DocumentRepository documentRepository;

    public SubmissionService(LoanApplicationService loanApplicationService, DocumentRepository documentRepository) {
        this.loanApplicationService = loanApplicationService;
        this.documentRepository = documentRepository;
    }

    @Transactional
    public SubmitResponse submit(UUID userId, UUID applicationId) {
        LoanApplication application = loanApplicationService.requireOwn(userId, applicationId);
        if (!application.isDraft()) {
            throw new InvalidApplicationStateException("This application has already been submitted");
        }

        validate(application);

        LoanConfig.Config config = LoanConfig.of(application.getLoanType());
        EmiCalculator.Breakdown breakdown = EmiCalculator.calculate(
                application.getLoanAmount(), config.interestRate(), application.getLoanTenureMonths());

        String referenceNumber = generateReferenceNumber();
        application.markSubmitted(
                referenceNumber,
                breakdown.emi(),
                breakdown.interestRate(),
                breakdown.processingFee(),
                breakdown.totalInterest(),
                breakdown.totalPayable()
        );
        loanApplicationService.save(application);

        return new SubmitResponse(
                referenceNumber,
                breakdown.emi(),
                breakdown.interestRate(),
                breakdown.processingFee(),
                breakdown.totalInterest(),
                breakdown.totalPayable(),
                application.getSubmittedAt()
        );
    }

    private void validate(LoanApplication application) {
        if (application.getLoanType() == null) {
            throw new SubmissionValidationException("Loan type is required");
        }
        LoanConfig.Config config = LoanConfig.of(application.getLoanType());

        BigDecimal amount = application.getLoanAmount();
        if (amount == null || amount.compareTo(config.minAmount()) < 0 || amount.compareTo(config.maxAmount()) > 0) {
            throw new SubmissionValidationException("Loan amount is outside the allowed range for this loan type");
        }

        Integer tenure = application.getLoanTenureMonths();
        if (tenure == null || tenure < config.minTenureMonths() || tenure > config.maxTenureMonths()) {
            throw new SubmissionValidationException("Loan tenure is outside the allowed range for this loan type");
        }

        if (!application.isKraPinVerified()) {
            throw new SubmissionValidationException("KRA PIN must be verified before submission");
        }
        if (!application.isNationalIdVerified()) {
            throw new SubmissionValidationException("National ID must be verified before submission");
        }
        if (!application.isIdConsent()) {
            throw new SubmissionValidationException("ID consent is required before submission");
        }

        if (application.getConsents() == null
                || !Boolean.TRUE.equals(application.getConsents().consentAccurate())
                || !Boolean.TRUE.equals(application.getConsents().consentCreditCheck())
                || !Boolean.TRUE.equals(application.getConsents().consentTerms())) {
            throw new SubmissionValidationException("All required consents must be accepted before submission");
        }

        if (LoanConfig.isCoApplicantRequired(application.getLoanType(), amount)) {
            if (application.getCoApplicant() == null
                    || application.getCoApplicant().coApplicantName() == null
                    || application.getCoApplicant().coApplicantName().isBlank()
                    || !Boolean.TRUE.equals(application.getCoApplicant().coApplicantConsent())) {
                throw new SubmissionValidationException("Co-applicant details and consent are required for this loan amount");
            }
        }

        String employmentType = application.getEmployment() != null ? application.getEmployment().employmentType() : null;
        Set<String> requiredDocs = RequiredDocuments.forApplication(application.getLoanType(), employmentType);
        Set<String> uploadedKeys = documentRepository.findAllByLoanApplicationId(application.getId()).stream()
                .map(d -> d.getDocumentKey())
                .collect(Collectors.toSet());

        Set<String> missing = requiredDocs.stream().filter(k -> !uploadedKeys.contains(k)).collect(Collectors.toSet());
        if (!missing.isEmpty()) {
            throw new SubmissionValidationException("Missing required documents: " + String.join(", ", missing));
        }
    }

    private String generateReferenceNumber() {
        StringBuilder sb = new StringBuilder("LS-");
        for (int i = 0; i < 8; i++) {
            sb.append(REFERENCE_ALPHABET.charAt(RANDOM.nextInt(REFERENCE_ALPHABET.length())));
        }
        return sb.toString();
    }
}
