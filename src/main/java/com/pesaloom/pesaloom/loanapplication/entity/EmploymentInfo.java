package com.pesaloom.pesaloom.loanapplication.entity;

public record EmploymentInfo(
        String employmentType,
        String companyName,
        String designation,
        Double monthlyNetSalary,
        Integer yearsOfExperience,
        String businessName,
        String businessType,
        Double annualTurnover,
        Integer yearsInBusiness,
        Double monthlyIncome,
        String businessKraPin,
        String officeAddress
) {
}
