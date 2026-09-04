package com.pesaloom.pesaloom.admin;

import com.pesaloom.pesaloom.admin.dto.AdminSavingsAccountDto;
import com.pesaloom.pesaloom.admin.dto.AdminSavingsSummaryDto;
import com.pesaloom.pesaloom.admin.dto.OpenSavingsAccountRequest;
import com.pesaloom.pesaloom.admin.dto.SavingsTransactionRequest;
import com.pesaloom.pesaloom.security.AuthPrincipal;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin/savings")
@PreAuthorize("hasRole('ADMIN')")
public class AdminSavingsController {

    private final AdminSavingsService adminSavingsService;

    public AdminSavingsController(AdminSavingsService adminSavingsService) {
        this.adminSavingsService = adminSavingsService;
    }

    @GetMapping
    public Page<AdminSavingsSummaryDto> list(
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        return adminSavingsService.list(search, pageable);
    }

    @PostMapping
    public AdminSavingsAccountDto open(@AuthenticationPrincipal AuthPrincipal principal,
                                        @Valid @RequestBody OpenSavingsAccountRequest request) {
        return adminSavingsService.open(request, principal.userId());
    }

    @GetMapping("/{id}")
    public AdminSavingsAccountDto get(@PathVariable UUID id) {
        return adminSavingsService.get(id);
    }

    @PostMapping("/{id}/deposits")
    public AdminSavingsAccountDto deposit(@AuthenticationPrincipal AuthPrincipal principal,
                                           @PathVariable UUID id,
                                           @Valid @RequestBody SavingsTransactionRequest request) {
        return adminSavingsService.deposit(id, request, principal.userId());
    }

    @PostMapping("/{id}/withdrawals")
    public AdminSavingsAccountDto withdraw(@AuthenticationPrincipal AuthPrincipal principal,
                                            @PathVariable UUID id,
                                            @Valid @RequestBody SavingsTransactionRequest request) {
        return adminSavingsService.withdraw(id, request, principal.userId());
    }
}
