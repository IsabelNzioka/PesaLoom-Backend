package com.pesaloom.pesaloom.auth;

import com.pesaloom.pesaloom.auth.dto.AuthResponse;
import com.pesaloom.pesaloom.auth.dto.ForgotPasswordRequest;
import com.pesaloom.pesaloom.auth.dto.LoginRequest;
import com.pesaloom.pesaloom.auth.dto.RefreshResponse;
import com.pesaloom.pesaloom.auth.dto.RegisterRequest;
import com.pesaloom.pesaloom.auth.dto.ResetPasswordRequest;
import com.pesaloom.pesaloom.auth.dto.UserSummaryDto;
import com.pesaloom.pesaloom.exception.InvalidCredentialsException;
import com.pesaloom.pesaloom.security.AuthPrincipal;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final String REFRESH_COOKIE_NAME = "refreshToken";
    private static final String REFRESH_COOKIE_PATH = "/api/auth";

    private final AuthService authService;
    private final long refreshTokenTtlDays;

    public AuthController(AuthService authService, @Value("${app.jwt.refresh-token-ttl-days}") long refreshTokenTtlDays) {
        this.authService = authService;
        this.refreshTokenTtlDays = refreshTokenTtlDays;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        var result = authService.register(request);
        return withRefreshCookie(result.rawRefreshToken())
                .body(new AuthResponse(result.accessToken(), result.user()));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        var result = authService.login(request.email(), request.password());
        return withRefreshCookie(result.rawRefreshToken())
                .body(new AuthResponse(result.accessToken(), result.user()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshResponse> refresh(
            @CookieValue(value = REFRESH_COOKIE_NAME, required = false) String refreshToken
    ) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new InvalidCredentialsException("No refresh token present");
        }
        var result = authService.refresh(refreshToken);
        return withRefreshCookie(result.rawRefreshToken())
                .body(new RefreshResponse(result.accessToken(), result.user()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @CookieValue(value = REFRESH_COOKIE_NAME, required = false) String refreshToken
    ) {
        authService.logout(refreshToken);
        return clearRefreshCookie().build();
    }

    @GetMapping("/me")
    public UserSummaryDto me(@AuthenticationPrincipal AuthPrincipal principal) {
        return authService.me(principal.userId());
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request.email());
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request.token(), request.newPassword());
        return ResponseEntity.noContent().build();
    }

    private ResponseEntity.BodyBuilder withRefreshCookie(String rawRefreshToken) {
        ResponseCookie cookie = ResponseCookie.from(REFRESH_COOKIE_NAME, rawRefreshToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path(REFRESH_COOKIE_PATH)
                .maxAge(Duration.ofDays(refreshTokenTtlDays))
                .build();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    private ResponseEntity.HeadersBuilder<?> clearRefreshCookie() {
        ResponseCookie cookie = ResponseCookie.from(REFRESH_COOKIE_NAME, "")
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path(REFRESH_COOKIE_PATH)
                .maxAge(Duration.ZERO)
                .build();
        return ResponseEntity.noContent().header(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}
