package com.pesaloom.pesaloom.admin;

import com.pesaloom.pesaloom.admin.dto.AddCollateralRequest;
import com.pesaloom.pesaloom.admin.dto.AdminDashboardSummaryDto;
import com.pesaloom.pesaloom.admin.dto.AdminLoanApplicationDto;
import com.pesaloom.pesaloom.admin.dto.AdminLoanApplicationSummaryDto;
import com.pesaloom.pesaloom.admin.dto.CollateralDto;
import com.pesaloom.pesaloom.admin.dto.StaffSummaryDto;
import com.pesaloom.pesaloom.admin.dto.StatusTransitionRequest;
import com.pesaloom.pesaloom.loanapplication.entity.LoanApplicationStatus;
import com.pesaloom.pesaloom.loanapplication.entity.LoanType;
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
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminApplicationController {

    private final AdminApplicationService adminApplicationService;
    private final AdminCollateralService adminCollateralService;

    public AdminApplicationController(AdminApplicationService adminApplicationService,
                                       AdminCollateralService adminCollateralService) {
        this.adminApplicationService = adminApplicationService;
        this.adminCollateralService = adminCollateralService;
    }

    @GetMapping("/loan-applications")
    public Page<AdminLoanApplicationSummaryDto> list(
            @RequestParam(required = false) LoanApplicationStatus status,
            @RequestParam(required = false) LoanType loanType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) UUID branchId,
            @PageableDefault(size = 20, sort = "submittedAt") Pageable pageable
    ) {
        return adminApplicationService.list(status, loanType, from, to, search, branchId, pageable);
    }

    @GetMapping("/loan-applications/{id}")
    public AdminLoanApplicationDto get(@PathVariable UUID id) {
        return adminApplicationService.get(id);
    }

    @PostMapping("/loan-applications/{id}/transition")
    public AdminLoanApplicationDto transition(@AuthenticationPrincipal AuthPrincipal principal,
                                               @PathVariable UUID id,
                                               @Valid @RequestBody StatusTransitionRequest request) {
        return adminApplicationService.transition(principal.userId(), id, request.targetStatus(), request.reviewNotes());
    }

    @PostMapping("/loan-applications/borrowers")
    public AdminLoanApplicationDto createBorrower(
            @RequestParam String businessName,
            @RequestParam String workingStatus,
            @RequestParam(required = false) String description,
            @RequestParam LoanType loanType,
            @RequestParam BigDecimal loanAmount,
            @RequestParam Integer loanTenureMonths,
            @RequestParam UUID branchId,
            @RequestParam(required = false) List<UUID> staffAccessUserIds,
            @RequestParam(required = false) MultipartFile photo,
            @RequestParam(required = false) List<MultipartFile> files
    ) {
        return adminApplicationService.createBorrower(businessName, workingStatus, description,
                loanType, loanAmount, loanTenureMonths, branchId, staffAccessUserIds, photo, files);
    }

    @GetMapping("/dashboard-summary")
    public AdminDashboardSummaryDto dashboardSummary(
            @RequestParam(required = false) LoanType loanType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to,
            @RequestParam(required = false) UUID branchId
    ) {
        return adminApplicationService.dashboardSummary(loanType, from, to, branchId);
    }

    @GetMapping("/staff")
    public List<StaffSummaryDto> staff() {
        return adminApplicationService.listStaff();
    }

    @GetMapping("/loan-applications/{id}/collateral")
    public List<CollateralDto> collateral(@PathVariable UUID id) {
        return adminCollateralService.listForApplication(id);
    }

    @PostMapping("/loan-applications/{id}/collateral")
    public CollateralDto addCollateral(@AuthenticationPrincipal AuthPrincipal principal,
                                        @PathVariable UUID id,
                                        @Valid @RequestBody AddCollateralRequest request) {
        return adminCollateralService.add(id, request, principal.userId());
    }
}
