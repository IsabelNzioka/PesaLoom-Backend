package com.lendswift.lendswift.auth;

import com.lendswift.lendswift.auth.entity.RefreshToken;
import com.lendswift.lendswift.auth.repository.RefreshTokenRepository;
import com.lendswift.lendswift.exception.InvalidCredentialsException;
import com.lendswift.lendswift.exception.TokenReuseDetectedException;
import com.lendswift.lendswift.user.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final long refreshTokenTtlDays;

    public RefreshTokenService(
            RefreshTokenRepository refreshTokenRepository,
            @Value("${app.jwt.refresh-token-ttl-days}") long refreshTokenTtlDays
    ) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.refreshTokenTtlDays = refreshTokenTtlDays;
    }

    public record IssuedRefreshToken(String rawValue, RefreshToken entity) {
    }


    @Transactional
    public IssuedRefreshToken issueNewFamily(User user) {
        return issue(user, UUID.randomUUID());
    }

    private IssuedRefreshToken issue(User user, UUID familyId) {
        String rawValue = RawTokenGenerator.generate();
        String tokenHash = RawTokenGenerator.hash(rawValue);
        RefreshToken entity = new RefreshToken(user, tokenHash, familyId, Instant.now().plusSeconds(refreshTokenTtlDays * 86400));
        refreshTokenRepository.save(entity);
        return new IssuedRefreshToken(rawValue, entity);
    }


    @Transactional(noRollbackFor = TokenReuseDetectedException.class)
    public IssuedRefreshToken rotate(String rawIncomingToken) {
        String tokenHash = RawTokenGenerator.hash(rawIncomingToken);
        RefreshToken current = refreshTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(InvalidCredentialsException::new);

        Instant now = Instant.now();
        if (current.isRevoked()) {
            refreshTokenRepository.revokeFamily(current.getFamilyId(), now);
            throw new TokenReuseDetectedException();
        }
        if (current.isExpired(now)) {
            throw new InvalidCredentialsException();
        }

        current.revoke();
        IssuedRefreshToken next = issue(current.getUser(), current.getFamilyId());
        current.markReplacedBy(next.entity().getId());
        refreshTokenRepository.save(current);
        return next;
    }


    @Transactional
    public void revoke(String rawToken) {
        String tokenHash = RawTokenGenerator.hash(rawToken);
        refreshTokenRepository.findByTokenHash(tokenHash).ifPresent(token -> {
            token.revoke();
            refreshTokenRepository.save(token);
        });
    }
}
