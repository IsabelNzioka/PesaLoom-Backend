package com.pesaloom.pesaloom.exception;

import org.springframework.http.HttpStatus;

public class InsufficientBalanceException extends ApiException {

    public InsufficientBalanceException() {
        super(HttpStatus.CONFLICT, "This withdrawal would exceed the account balance");
    }
}
