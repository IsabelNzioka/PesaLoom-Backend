package com.pesaloom.pesaloom.admin;

import com.pesaloom.pesaloom.admin.dto.CollateralDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/collateral")
@PreAuthorize("hasRole('ADMIN')")
public class AdminCollateralRegisterController {

    private final AdminCollateralService adminCollateralService;

    public AdminCollateralRegisterController(AdminCollateralService adminCollateralService) {
        this.adminCollateralService = adminCollateralService;
    }

    @GetMapping
    public Page<CollateralDto> list(
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        return adminCollateralService.listAll(search, pageable);
    }
}
