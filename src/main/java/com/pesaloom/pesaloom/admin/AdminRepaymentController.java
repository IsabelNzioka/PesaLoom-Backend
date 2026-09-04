package com.pesaloom.pesaloom.admin;

import com.pesaloom.pesaloom.admin.dto.AdminRepaymentDto;
import com.pesaloom.pesaloom.loan.entity.RepaymentMethod;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/api/admin/repayments")
@PreAuthorize("hasRole('ADMIN')")
public class AdminRepaymentController {

    private final AdminRepaymentService adminRepaymentService;

    public AdminRepaymentController(AdminRepaymentService adminRepaymentService) {
        this.adminRepaymentService = adminRepaymentService;
    }

    @GetMapping
    public Page<AdminRepaymentDto> list(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to,
            @RequestParam(required = false) RepaymentMethod method,
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        return adminRepaymentService.list(from, to, method, search, pageable);
    }
}
