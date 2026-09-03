package com.lendswift.lendswift.loanapplication;

import java.util.regex.Pattern;


public final class KenyaFormats {

    public static final Pattern KRA_PIN = Pattern.compile("^[A-Z]\\d{9}[A-Z]$");
    public static final Pattern NATIONAL_ID = Pattern.compile("^\\d{7,8}$");
    public static final Pattern MOBILE = Pattern.compile("^0[17]\\d{8}$");
    public static final Pattern POSTAL_CODE = Pattern.compile("^\\d{5}$");

    private KenyaFormats() {
    }
}
