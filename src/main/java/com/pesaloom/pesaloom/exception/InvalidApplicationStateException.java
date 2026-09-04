package com.pesaloom.pesaloom.exception;

import org.springframework.http.HttpStatus;

public class InvalidApplicationStateException extends ApiException {

    public InvalidApplicationStateException(String message) {
        super(HttpStatus.CONFLICT, message);
    }
}
