package com.pesaloom.pesaloom.exception;

import org.springframework.http.HttpStatus;

public class InvalidStatusTransitionException extends ApiException {

    public InvalidStatusTransitionException(String message) {
        super(HttpStatus.CONFLICT, message);
    }
}
