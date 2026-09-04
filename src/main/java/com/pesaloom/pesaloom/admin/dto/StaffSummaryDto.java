package com.pesaloom.pesaloom.admin.dto;

import com.pesaloom.pesaloom.user.User;

import java.util.UUID;

public record StaffSummaryDto(
        UUID id,
        String firstName,
        String lastName,
        String email
) {
    public static StaffSummaryDto from(User user) {
        return new StaffSummaryDto(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail());
    }
}
