package com.pesaloom.pesaloom.exception;

import org.springframework.http.HttpStatus;

public class LoanApplicationNotFoundException extends ApiException {

    public LoanApplicationNotFoundException() {
        super(HttpStatus.NOT_FOUND, "Loan application not found");
    }
}
