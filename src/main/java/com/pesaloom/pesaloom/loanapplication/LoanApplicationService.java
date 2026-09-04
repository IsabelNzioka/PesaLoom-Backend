package com.pesaloom.pesaloom.loanapplication;

import com.pesaloom.pesaloom.branch.repository.BranchRepository;
import com.pesaloom.pesaloom.exception.LoanApplicationNotFoundException;
import com.pesaloom.pesaloom.loanapplication.dto.DocumentDto;
import com.pesaloom.pesaloom.loanapplication.dto.LoanApplicationDto;
import com.pesaloom.pesaloom.loanapplication.dto.SaveDraftRequest;
import com.pesaloom.pesaloom.loanapplication.entity.LoanApplication;
import com.pesaloom.pesaloom.loanapplication.entity.LoanApplicationStatus;
import com.pesaloom.pesaloom.loanapplication.entity.LoanType;
import com.pesaloom.pesaloom.loanapplication.repository.DocumentRepository;
import com.pesaloom.pesaloom.loanapplication.repository.LoanApplicationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class LoanApplicationService {

    private static final String DEFAULT_BRANCH_CODE = "MAIN";

    private final LoanApplicationRepository loanApplicationRepository;
    private final DocumentRepository documentRepository;
    private final BranchRepository branchRepository;

    public LoanApplicationService(LoanApplicationRepository loanApplicationRepository,
                                   DocumentRepository documentRepository,
                                   BranchRepository branchRepository) {
        this.loanApplicationRepository = loanApplicationRepository;
        this.documentRepository = documentRepository;
        this.branchRepository = branchRepository;
    }


    @Transactional
    public LoanApplication getOrCreateDraft(UUID userId) {
        return loanApplicationRepository.findByUserIdAndStatus(userId, LoanApplicationStatus.DRAFT)
                .orElseGet(() -> {
                    LoanApplication draft = new LoanApplication(userId);
                    branchRepository.findByCode(DEFAULT_BRANCH_CODE).ifPresent(b -> draft.setBranchId(b.getId()));
                    return loanApplicationRepository.save(draft);
                });
    }

    public LoanApplicationDto getDraft(UUID userId) {
        LoanApplication draft = loanApplicationRepository.findByUserIdAndStatus(userId, LoanApplicationStatus.DRAFT)
                .orElseThrow(LoanApplicationNotFoundException::new);
        return toDto(draft);
    }

    @Transactional
    public LoanApplicationDto saveDraft(UUID userId, SaveDraftRequest request) {
        LoanApplication draft = getOrCreateDraft(userId);

        if (request.loanType() != null && !request.loanType().isBlank()) {
            draft.setLoanType(LoanType.valueOf(request.loanType()));
        }
        if (request.loanAmount() != null) draft.setLoanAmount(request.loanAmount());
        if (request.loanTenureMonths() != null) draft.setLoanTenureMonths(request.loanTenureMonths());
        if (request.loanPurpose() != null) draft.setLoanPurpose(request.loanPurpose());
        if (request.referralCode() != null) draft.setReferralCode(request.referralCode());
        if (request.currentStep() != null) draft.setCurrentStep(request.currentStep());
        if (request.kraPin() != null) draft.setKraPin(request.kraPin());
        if (request.nationalId() != null) draft.setNationalId(request.nationalId());
        if (request.idConsent() != null) draft.setIdConsent(request.idConsent());
        if (request.personalInfo() != null) draft.setPersonalInfo(request.personalInfo());
        if (request.address() != null) draft.setAddress(request.address());
        if (request.employment() != null) draft.setEmployment(request.employment());
        if (request.coApplicant() != null) draft.setCoApplicant(request.coApplicant());
        if (request.disbursement() != null) draft.setDisbursement(request.disbursement());
        if (request.consents() != null) draft.setConsents(request.consents());
        draft.touch();

        return toDto(loanApplicationRepository.save(draft));
    }

    @Transactional
    public void discardDraft(UUID userId) {
        loanApplicationRepository.findByUserIdAndStatus(userId, LoanApplicationStatus.DRAFT)
                .ifPresent(loanApplicationRepository::delete);
    }

    public LoanApplicationDto getOwnApplication(UUID userId, UUID applicationId) {
        LoanApplication application = loanApplicationRepository.findByIdAndUserId(applicationId, userId)
                .orElseThrow(LoanApplicationNotFoundException::new);
        return toDto(application);
    }

    public List<LoanApplicationDto> listOwnApplications(UUID userId) {
        return loanApplicationRepository.findAllByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::toDto)
                .toList();
    }


    public LoanApplication requireOwn(UUID userId, UUID applicationId) {
        return loanApplicationRepository.findByIdAndUserId(applicationId, userId)
                .orElseThrow(LoanApplicationNotFoundException::new);
    }

    public LoanApplication save(LoanApplication application) {
        return loanApplicationRepository.save(application);
    }

    public LoanApplicationDto toDto(LoanApplication a) {
        List<DocumentDto> documents = documentRepository.findAllByLoanApplicationId(a.getId()).stream()
                .map(d -> new DocumentDto(d.getId(), d.getDocumentKey(), d.getOriginalFilename(),
                        d.getContentType(), d.getSizeBytes(), d.getUploadedAt()))
                .toList();

        return new LoanApplicationDto(
                a.getId(),
                a.getStatus().name(),
                a.getLoanType() != null ? a.getLoanType().name() : null,
                a.getLoanAmount(),
                a.getLoanTenureMonths(),
                a.getLoanPurpose(),
                a.getReferralCode(),
                a.getReferenceNumber(),
                a.getCurrentStep(),
                a.getKraPin(),
                a.isKraPinVerified(),
                a.getNationalId(),
                a.isNationalIdVerified(),
                a.isIdConsent(),
                a.getPersonalInfo(),
                a.getAddress(),
                a.getEmployment(),
                a.getCoApplicant(),
                a.getDisbursement(),
                a.getConsents(),
                a.getEmi(),
                a.getInterestRate(),
                a.getProcessingFee(),
                a.getTotalInterest(),
                a.getTotalPayable(),
                a.getSubmittedAt(),
                a.getUpdatedAt(),
                documents
        );
    }
}
