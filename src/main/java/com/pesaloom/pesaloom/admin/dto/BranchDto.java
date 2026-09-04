package com.pesaloom.pesaloom.admin.dto;

import java.util.UUID;

public record BranchDto(
        UUID id,
        String name,
        String code,
        String address,
        String phone,
        long staffCount,
        long applicationsCount,
        long activeLoansCount
) {
}
