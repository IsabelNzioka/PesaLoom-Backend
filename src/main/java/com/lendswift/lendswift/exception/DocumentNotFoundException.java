package com.lendswift.lendswift.exception;

import org.springframework.http.HttpStatus;

public class DocumentNotFoundException extends ApiException {

    public DocumentNotFoundException() {
        super(HttpStatus.NOT_FOUND, "Document not found");
    }
}
