package com.lendswift.lendswift.loanapplication;

import com.lendswift.lendswift.loanapplication.dto.VerifyResponse;
import com.lendswift.lendswift.loanapplication.entity.LoanApplication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;


@Service
public class VerificationService {

    private static final long SIMULATED_DELAY_MS = 1200;

    private final LoanApplicationService loanApplicationService;

    public VerificationService(LoanApplicationService loanApplicationService) {
        this.loanApplicationService = loanApplicationService;
    }

    @Transactional
    public VerifyResponse verifyKraPin(UUID userId, String kraPin) {
        boolean formatValid = KenyaFormats.KRA_PIN.matcher(kraPin).matches();
        simulateDelay();

        LoanApplication draft = loanApplicationService.getOrCreateDraft(userId);
        draft.setKraPin(kraPin);
        draft.setKraPinVerified(formatValid, formatValid ? Instant.now() : null);
        draft.touch();
        loanApplicationService.save(draft);

        return formatValid
                ? new VerifyResponse(true, "KRA PIN verified")
                : new VerifyResponse(false, "We couldn't verify that KRA PIN. Check the format and try again.");
    }

    @Transactional
    public VerifyResponse verifyNationalId(UUID userId, String nationalId) {
        boolean formatValid = KenyaFormats.NATIONAL_ID.matcher(nationalId).matches();
        simulateDelay();

        LoanApplication draft = loanApplicationService.getOrCreateDraft(userId);
        draft.setNationalId(nationalId);
        draft.setNationalIdVerified(formatValid, formatValid ? Instant.now() : null);
        draft.touch();
        loanApplicationService.save(draft);

        return formatValid
                ? new VerifyResponse(true, "National ID verified")
                : new VerifyResponse(false, "We couldn't verify that National ID. Check the format and try again.");
    }

    private void simulateDelay() {
        try {
            Thread.sleep(SIMULATED_DELAY_MS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
