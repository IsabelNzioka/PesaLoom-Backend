package com.pesaloom.pesaloom.exception;

import org.springframework.http.HttpStatus;

public class TokenReuseDetectedException extends ApiException {

    public TokenReuseDetectedException() {
        super(HttpStatus.UNAUTHORIZED, "Session invalidated. Please log in again");
    }
}
