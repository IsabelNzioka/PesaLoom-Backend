package com.lendswift.lendswift.exception;

import org.springframework.http.HttpStatus;

public class InvalidOrExpiredTokenException extends ApiException {

    public InvalidOrExpiredTokenException() {
        super(HttpStatus.BAD_REQUEST, "This link is invalid or has expired");
    }
}
