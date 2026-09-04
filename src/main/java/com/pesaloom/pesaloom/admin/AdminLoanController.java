package com.pesaloom.pesaloom.admin;

import com.pesaloom.pesaloom.admin.dto.AdminLoanDto;
import com.pesaloom.pesaloom.admin.dto.AdminLoanSummaryDto;
import com.pesaloom.pesaloom.admin.dto.CollectionSheetRowDto;
import com.pesaloom.pesaloom.admin.dto.LoanCalculatorResponseDto;
import com.pesaloom.pesaloom.admin.dto.RecordRepaymentRequest;
import com.pesaloom.pesaloom.security.AuthPrincipal;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/loans")
@PreAuthorize("hasRole('ADMIN')")
public class AdminLoanController {

    private final AdminLoanService adminLoanService;

    public AdminLoanController(AdminLoanService adminLoanService) {
        this.adminLoanService = adminLoanService;
    }

    @GetMapping
    public Page<AdminLoanSummaryDto> list(
            @RequestParam(required = false) String view,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) UUID branchId,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        return adminLoanService.list(view, search, branchId, pageable);
    }

    @GetMapping("/calculator")
    public LoanCalculatorResponseDto calculator(
            @RequestParam BigDecimal principal,
            @RequestParam BigDecimal interestRatePercent,
            @RequestParam int tenureMonths
    ) {
        return adminLoanService.calculate(principal, interestRatePercent, tenureMonths);
    }

    @GetMapping("/collection-sheet")
    public List<CollectionSheetRowDto> collectionSheet(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        return adminLoanService.collectionSheet(from, to);
    }

    @GetMapping("/{id}")
    public AdminLoanDto get(@PathVariable UUID id) {
        return adminLoanService.get(id);
    }

    @PostMapping("/{id}/repayments")
    public AdminLoanDto recordRepayment(@AuthenticationPrincipal AuthPrincipal principal,
                                         @PathVariable UUID id,
                                         @Valid @RequestBody RecordRepaymentRequest request) {
        return adminLoanService.recordRepayment(id, request, principal.userId());
    }
}
