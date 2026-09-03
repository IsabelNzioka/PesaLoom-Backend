package com.lendswift.lendswift.auth.dto;

public record RefreshResponse(String accessToken, UserSummaryDto user) {
}
