package com.pesaloom.pesaloom.auth.dto;

public record AuthResponse(String accessToken, UserSummaryDto user) {
}
