package com.pesaloom.pesaloom.loanapplication;

import java.security.SecureRandom;


public final class ReferenceNumberGenerator {

    private static final String ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"; // no 0/O/1/I
    private static final SecureRandom RANDOM = new SecureRandom();

    public static String generate() {
        return generate("LS");
    }

    public static String generate(String prefix) {
        StringBuilder sb = new StringBuilder(prefix).append('-');
        for (int i = 0; i < 8; i++) {
            sb.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }
        return sb.toString();
    }

    private ReferenceNumberGenerator() {
    }
}
