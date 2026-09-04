package com.pesaloom.pesaloom.exception;

import org.springframework.http.HttpStatus;

public class LoanNotFoundException extends ApiException {

    public LoanNotFoundException() {
        super(HttpStatus.NOT_FOUND, "Loan not found");
    }
}
