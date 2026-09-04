package com.pesaloom.pesaloom.loanapplication.entity;

public record PersonalInfo(
        String fullName,
        String fatherName,
        String motherName,
        String dateOfBirth,
        String gender,
        String maritalStatus,
        String email,
        String mobile,
        String alternateMobile
) {
}
