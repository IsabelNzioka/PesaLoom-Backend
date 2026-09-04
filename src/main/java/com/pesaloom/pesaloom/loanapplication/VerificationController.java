package com.pesaloom.pesaloom.loanapplication;

import com.pesaloom.pesaloom.loanapplication.dto.VerifyKraPinRequest;
import com.pesaloom.pesaloom.loanapplication.dto.VerifyNationalIdRequest;
import com.pesaloom.pesaloom.loanapplication.dto.VerifyResponse;
import com.pesaloom.pesaloom.security.AuthPrincipal;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/loan-applications")
public class VerificationController {

    private final VerificationService verificationService;

    public VerificationController(VerificationService verificationService) {
        this.verificationService = verificationService;
    }

    @PostMapping("/verify-kra-pin")
    public VerifyResponse verifyKraPin(@AuthenticationPrincipal AuthPrincipal principal,
                                        @Valid @RequestBody VerifyKraPinRequest request) {
        return verificationService.verifyKraPin(principal.userId(), request.kraPin());
    }

    @PostMapping("/verify-national-id")
    public VerifyResponse verifyNationalId(@AuthenticationPrincipal AuthPrincipal principal,
                                            @Valid @RequestBody VerifyNationalIdRequest request) {
        return verificationService.verifyNationalId(principal.userId(), request.nationalId());
    }
}
