package com.pesaloom.pesaloom.exception;

import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class AccountLockedException extends ApiException {

    public AccountLockedException(Instant lockedUntil) {
        super(HttpStatus.FORBIDDEN, "Account locked due to too many failed login attempts. Try again in "
                + Math.max(1, ChronoUnit.MINUTES.between(Instant.now(), lockedUntil)) + " minute(s)");
    }
}
