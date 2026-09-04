package com.pesaloom.pesaloom.loanapplication;

import com.pesaloom.pesaloom.exception.InvalidApplicationStateException;
import com.pesaloom.pesaloom.exception.SubmissionValidationException;
import com.pesaloom.pesaloom.loanapplication.dto.SubmitResponse;
import com.pesaloom.pesaloom.loanapplication.entity.LoanApplication;
import com.pesaloom.pesaloom.loanapplication.repository.DocumentRepository;
import com.pesaloom.pesaloom.notification.LoanSubmissionNotifier;
import com.pesaloom.pesaloom.notification.dto.LoanSubmissionEmailData;
import com.pesaloom.pesaloom.pdf.LoanSubmissionPdfGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SubmissionService {

    private final LoanApplicationService loanApplicationService;
    private final DocumentRepository documentRepository;
    private final LoanSubmissionPdfGenerator pdfGenerator;
    private final LoanSubmissionNotifier notifier;

    public SubmissionService(LoanApplicationService loanApplicationService, DocumentRepository documentRepository,
                              LoanSubmissionPdfGenerator pdfGenerator, LoanSubmissionNotifier notifier) {
        this.loanApplicationService = loanApplicationService;
        this.documentRepository = documentRepository;
        this.pdfGenerator = pdfGenerator;
        this.notifier = notifier;
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

        String referenceNumber = ReferenceNumberGenerator.generate();
        application.markSubmitted(
                referenceNumber,
                breakdown.emi(),
                breakdown.interestRate(),
                breakdown.processingFee(),
                breakdown.totalInterest(),
                breakdown.totalPayable()
        );
        loanApplicationService.save(application);

        notifySubmission(application);

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


    private void notifySubmission(LoanApplication application) {
        if (application.getPersonalInfo() == null || application.getPersonalInfo().email() == null) {
            return;
        }
        byte[] pdf = pdfGenerator.generate(application);
        LoanSubmissionEmailData data = new LoanSubmissionEmailData(
                application.getPersonalInfo().email(),
                application.getPersonalInfo().fullName(),
                application.getReferenceNumber(),
                application.getLoanType() != null ? application.getLoanType().name() : null,
                application.getLoanAmount(),
                application.getLoanTenureMonths() != null ? application.getLoanTenureMonths() : 0,
                application.getEmi(),
                application.getInterestRate(),
                application.getProcessingFee(),
                application.getTotalInterest(),
                application.getTotalPayable(),
                application.getSubmittedAt()
        );
        notifier.notifyAsync(data, pdf);
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
}
