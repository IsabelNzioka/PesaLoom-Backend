package com.pesaloom.pesaloom.auth.dto;

import com.pesaloom.pesaloom.user.Role;
import com.pesaloom.pesaloom.user.User;

import java.util.UUID;

public record UserSummaryDto(
        UUID id,
        String email,
        String firstName,
        String lastName,
        Role role
) {
    public static UserSummaryDto from(User user) {
        return new UserSummaryDto(user.getId(), user.getEmail(), user.getFirstName(), user.getLastName(), user.getRole());
    }
}
