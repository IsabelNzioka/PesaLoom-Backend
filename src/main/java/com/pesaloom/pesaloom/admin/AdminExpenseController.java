package com.pesaloom.pesaloom.admin;

import com.pesaloom.pesaloom.admin.dto.ExpenseDto;
import com.pesaloom.pesaloom.admin.dto.RecordExpenseRequest;
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

import java.util.List;

@RestController
@RequestMapping("/api/admin/expenses")
@PreAuthorize("hasRole('ADMIN')")
public class AdminExpenseController {

    private final AdminExpenseService adminExpenseService;

    public AdminExpenseController(AdminExpenseService adminExpenseService) {
        this.adminExpenseService = adminExpenseService;
    }

    @GetMapping
    public Page<ExpenseDto> list(
            @RequestParam(required = false) String category,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        return adminExpenseService.list(category, pageable);
    }

    @GetMapping("/categories")
    public List<String> categories() {
        return AdminExpenseService.CATEGORIES;
    }

    @PostMapping
    public ExpenseDto record(@AuthenticationPrincipal AuthPrincipal principal,
                              @Valid @RequestBody RecordExpenseRequest request) {
        return adminExpenseService.record(request, principal.userId());
    }
}
