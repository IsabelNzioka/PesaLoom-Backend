package com.pesaloom.pesaloom.admin;

import com.pesaloom.pesaloom.admin.dto.BranchDto;
import com.pesaloom.pesaloom.admin.dto.CreateBranchRequest;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/branches")
@PreAuthorize("hasRole('ADMIN')")
public class AdminBranchController {

    private final AdminBranchService adminBranchService;

    public AdminBranchController(AdminBranchService adminBranchService) {
        this.adminBranchService = adminBranchService;
    }

    @GetMapping
    public List<BranchDto> list() {
        return adminBranchService.list();
    }

    @PostMapping
    public BranchDto create(@Valid @RequestBody CreateBranchRequest request) {
        return adminBranchService.create(request);
    }
}
