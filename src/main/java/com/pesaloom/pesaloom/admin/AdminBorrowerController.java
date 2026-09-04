package com.pesaloom.pesaloom.admin;

import com.pesaloom.pesaloom.admin.dto.AdminBorrowerDetailDto;
import com.pesaloom.pesaloom.admin.dto.AdminBorrowerSummaryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin/borrowers")
@PreAuthorize("hasRole('ADMIN')")
public class AdminBorrowerController {

    private final AdminBorrowerService adminBorrowerService;

    public AdminBorrowerController(AdminBorrowerService adminBorrowerService) {
        this.adminBorrowerService = adminBorrowerService;
    }

    @GetMapping
    public Page<AdminBorrowerSummaryDto> list(
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        return adminBorrowerService.list(search, pageable);
    }

    @GetMapping("/{userId}")
    public AdminBorrowerDetailDto get(@PathVariable UUID userId) {
        return adminBorrowerService.get(userId);
    }
}
