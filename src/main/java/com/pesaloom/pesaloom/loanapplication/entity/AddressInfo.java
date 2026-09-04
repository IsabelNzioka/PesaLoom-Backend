package com.pesaloom.pesaloom.loanapplication.entity;

public record AddressInfo(
        String currentAddressLine1,
        String currentAddressLine2,
        String postalCode,
        String city,
        String county,
        String residenceType,
        Double rentAmount,
        Integer yearsAtAddress,
        String previousAddress,
        Boolean sameAsPermanent,
        String permanentAddressLine1,
        String permanentAddressLine2,
        String permanentPostalCode,
        String permanentCity,
        String permanentCounty
) {
}
