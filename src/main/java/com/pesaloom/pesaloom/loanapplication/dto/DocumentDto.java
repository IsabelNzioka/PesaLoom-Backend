package com.pesaloom.pesaloom.loanapplication.dto;

import java.time.Instant;
import java.util.UUID;

public record DocumentDto(
        UUID id,
        String documentKey,
        String originalFilename,
        String contentType,
        long sizeBytes,
        Instant uploadedAt
) {
}
