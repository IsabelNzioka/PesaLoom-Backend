package com.pesaloom.pesaloom.auth.dto;

public record RefreshResponse(String accessToken, UserSummaryDto user) {
}
