package com.pesaloom.pesaloom.loanapplication.entity;

public record DisbursementInfo(
        String disbursementMethod,
        String disbursementBankName,
        String disbursementAccountNumber,
        String disbursementAccountName
) {
}
