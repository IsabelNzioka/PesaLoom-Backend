package com.pesaloom.pesaloom.exception;

import org.springframework.http.HttpStatus;

public class EmailAlreadyInUseException extends ApiException {

    public EmailAlreadyInUseException() {
        super(HttpStatus.CONFLICT, "An account with this email already exists");
    }
}
