package com.pesaloom.pesaloom.admin;

import com.pesaloom.pesaloom.admin.dto.PayrollRecordDto;
import com.pesaloom.pesaloom.admin.dto.RecordPayrollRequest;
import com.pesaloom.pesaloom.security.AuthPrincipal;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/payroll")
@PreAuthorize("hasRole('ADMIN')")
public class AdminPayrollController {

    private final AdminPayrollService adminPayrollService;

    public AdminPayrollController(AdminPayrollService adminPayrollService) {
        this.adminPayrollService = adminPayrollService;
    }

    @GetMapping
    public Page<PayrollRecordDto> list(
            @RequestParam(required = false) String periodMonth,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        return adminPayrollService.list(periodMonth, pageable);
    }

    @PostMapping
    public PayrollRecordDto record(@AuthenticationPrincipal AuthPrincipal principal,
                                    @Valid @RequestBody RecordPayrollRequest request) {
        return adminPayrollService.record(request, principal.userId());
    }
}
