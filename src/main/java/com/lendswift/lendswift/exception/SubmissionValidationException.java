package com.lendswift.lendswift.exception;

import org.springframework.http.HttpStatus;

public class SubmissionValidationException extends ApiException {

    public SubmissionValidationException(String message) {
        super(HttpStatus.UNPROCESSABLE_ENTITY, message);
    }
}
