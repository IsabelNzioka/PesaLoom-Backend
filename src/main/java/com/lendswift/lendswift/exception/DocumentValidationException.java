package com.lendswift.lendswift.exception;

import org.springframework.http.HttpStatus;

public class DocumentValidationException extends ApiException {

    public DocumentValidationException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
