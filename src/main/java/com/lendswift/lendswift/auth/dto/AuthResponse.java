package com.lendswift.lendswift.auth.dto;

public record AuthResponse(String accessToken, UserSummaryDto user) {
}
