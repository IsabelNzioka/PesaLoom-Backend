package com.pesaloom.pesaloom.loanapplication;

import com.pesaloom.pesaloom.loanapplication.dto.LoanApplicationDto;
import com.pesaloom.pesaloom.loanapplication.dto.SaveDraftRequest;
import com.pesaloom.pesaloom.loanapplication.dto.SubmitResponse;
import com.pesaloom.pesaloom.security.AuthPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/loan-applications")
public class LoanApplicationController {

    private final LoanApplicationService loanApplicationService;
    private final SubmissionService submissionService;

    public LoanApplicationController(LoanApplicationService loanApplicationService,
                                      SubmissionService submissionService) {
        this.loanApplicationService = loanApplicationService;
        this.submissionService = submissionService;
    }

    @GetMapping("/draft")
    public LoanApplicationDto getDraft(@AuthenticationPrincipal AuthPrincipal principal) {
        return loanApplicationService.getDraft(principal.userId());
    }

    @PutMapping("/draft")
    public LoanApplicationDto saveDraft(@AuthenticationPrincipal AuthPrincipal principal,
                                         @RequestBody SaveDraftRequest request) {
        return loanApplicationService.saveDraft(principal.userId(), request);
    }

    @DeleteMapping("/draft")
    public ResponseEntity<Void> discardDraft(@AuthenticationPrincipal AuthPrincipal principal) {
        loanApplicationService.discardDraft(principal.userId());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/submit")
    public SubmitResponse submit(@AuthenticationPrincipal AuthPrincipal principal, @PathVariable UUID id) {
        return submissionService.submit(principal.userId(), id);
    }

    @GetMapping
    public List<LoanApplicationDto> list(@AuthenticationPrincipal AuthPrincipal principal) {
        return loanApplicationService.listOwnApplications(principal.userId());
    }

    @GetMapping("/{id}")
    public LoanApplicationDto get(@AuthenticationPrincipal AuthPrincipal principal, @PathVariable UUID id) {
        return loanApplicationService.getOwnApplication(principal.userId(), id);
    }
}
