package com.pesaloom.pesaloom.auth;

import com.pesaloom.pesaloom.auth.dto.RegisterRequest;
import com.pesaloom.pesaloom.auth.dto.UserSummaryDto;
import com.pesaloom.pesaloom.auth.entity.PasswordResetToken;
import com.pesaloom.pesaloom.auth.repository.PasswordResetTokenRepository;
import com.pesaloom.pesaloom.exception.AccountLockedException;
import com.pesaloom.pesaloom.exception.InvalidCredentialsException;
import com.pesaloom.pesaloom.exception.InvalidOrExpiredTokenException;
import com.pesaloom.pesaloom.exception.TokenReuseDetectedException;
import com.pesaloom.pesaloom.notification.NotificationService;
import com.pesaloom.pesaloom.security.JwtService;
import com.pesaloom.pesaloom.user.User;
import com.pesaloom.pesaloom.user.UserRepository;
import com.pesaloom.pesaloom.user.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class AuthService {

    private static final long RESET_TOKEN_TTL_MINUTES = 60;

    private final UserRepository userRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final NotificationService notificationService;
    private final int maxFailedAttempts;
    private final long lockoutMinutes;
    private final String frontendBaseUrl;

    public AuthService(
            UserRepository userRepository,
            UserService userService,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            RefreshTokenService refreshTokenService,
            PasswordResetTokenRepository passwordResetTokenRepository,
            NotificationService notificationService,
            @Value("${app.auth.max-failed-attempts}") int maxFailedAttempts,
            @Value("${app.auth.lockout-minutes}") long lockoutMinutes,
            @Value("${app.frontend.base-url}") String frontendBaseUrl
    ) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.notificationService = notificationService;
        this.maxFailedAttempts = maxFailedAttempts;
        this.lockoutMinutes = lockoutMinutes;
        this.frontendBaseUrl = frontendBaseUrl;
    }

    public record AuthResult(String accessToken, String rawRefreshToken, UserSummaryDto user) {
    }

    @Transactional
    public AuthResult register(RegisterRequest request) {
        User user = userService.register(request.email(), request.password(), request.firstName(), request.lastName());
        notificationService.sendWelcomeEmail(user.getEmail(), user.getFirstName());
        return issueSession(user);
    }


    @Transactional(noRollbackFor = {InvalidCredentialsException.class, AccountLockedException.class})
    public AuthResult login(String email, String rawPassword) {
        User user = userRepository.findByEmail(UserService.normalizeEmail(email))
                .orElseThrow(InvalidCredentialsException::new);

        Instant now = Instant.now();
        if (user.isLocked(now)) {
            throw new AccountLockedException(user.getLockedUntil());
        }

        if (!passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
            user.registerFailedLogin(maxFailedAttempts, lockoutMinutes);
            userRepository.save(user);
            if (user.isLocked(Instant.now())) {
                throw new AccountLockedException(user.getLockedUntil());
            }
            throw new InvalidCredentialsException();
        }

        user.registerSuccessfulLogin();
        userRepository.save(user);
        return issueSession(user);
    }


    @Transactional(noRollbackFor = TokenReuseDetectedException.class)
    public AuthResult refresh(String rawRefreshToken) {
        var rotated = refreshTokenService.rotate(rawRefreshToken);
        User user = rotated.entity().getUser();
        String accessToken = jwtService.generateAccessToken(user);
        return new AuthResult(accessToken, rotated.rawValue(), UserSummaryDto.from(user));
    }

    @Transactional(readOnly = true)
    public UserSummaryDto me(UUID userId) {
        User user = userRepository.findById(userId).orElseThrow(InvalidCredentialsException::new);
        return UserSummaryDto.from(user);
    }

    @Transactional
    public void logout(String rawRefreshToken) {
        if (rawRefreshToken != null && !rawRefreshToken.isBlank()) {
            refreshTokenService.revoke(rawRefreshToken);
        }
    }

    @Transactional
    public void forgotPassword(String email) {
        userRepository.findByEmail(UserService.normalizeEmail(email)).ifPresent(user -> {
            String rawToken = RawTokenGenerator.generate();
            String tokenHash = RawTokenGenerator.hash(rawToken);
            PasswordResetToken resetToken = new PasswordResetToken(
                    user, tokenHash, Instant.now().plusSeconds(RESET_TOKEN_TTL_MINUTES * 60));
            passwordResetTokenRepository.save(resetToken);
            String link = frontendBaseUrl + "/reset-password?token=" + rawToken;
            notificationService.sendPasswordResetLink(user.getEmail(), link);
        });

    }

    @Transactional
    public void resetPassword(String rawToken, String newPassword) {
        String tokenHash = RawTokenGenerator.hash(rawToken);
        PasswordResetToken resetToken = passwordResetTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(InvalidOrExpiredTokenException::new);

        if (!resetToken.isUsable(Instant.now())) {
            throw new InvalidOrExpiredTokenException();
        }

        userService.changePassword(resetToken.getUser(), newPassword);
        resetToken.markUsed();
        passwordResetTokenRepository.save(resetToken);
    }

    private AuthResult issueSession(User user) {
        String accessToken = jwtService.generateAccessToken(user);
        var issued = refreshTokenService.issueNewFamily(user);
        return new AuthResult(accessToken, issued.rawValue(), UserSummaryDto.from(user));
    }
}
