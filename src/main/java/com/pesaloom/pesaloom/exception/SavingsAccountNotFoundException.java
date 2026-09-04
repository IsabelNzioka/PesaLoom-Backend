package com.pesaloom.pesaloom.exception;

import org.springframework.http.HttpStatus;

public class SavingsAccountNotFoundException extends ApiException {

    public SavingsAccountNotFoundException() {
        super(HttpStatus.NOT_FOUND, "Savings account not found");
    }
}
