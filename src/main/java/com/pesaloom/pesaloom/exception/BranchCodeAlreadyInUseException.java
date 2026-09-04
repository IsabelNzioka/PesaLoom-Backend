package com.pesaloom.pesaloom.exception;

import org.springframework.http.HttpStatus;

public class BranchCodeAlreadyInUseException extends ApiException {

    public BranchCodeAlreadyInUseException() {
        super(HttpStatus.CONFLICT, "A branch with this code already exists");
    }
}
