package org.derleta.authorization.service.token;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.derleta.authorization.controller.mapper.TokenApiMapper;
import org.derleta.authorization.domain.entity.token.TokenEntity;
import org.derleta.authorization.domain.model.RefreshToken;
import org.derleta.authorization.repository.TokenRepositoryInterface;
import org.derleta.authorization.repository.impl.UserRepository;
import org.derleta.authorization.repository.impl.token.RefreshTokenRepository;
import org.derleta.authorization.security.api.UserSecurityService;
import org.derleta.authorization.security.model.RefreshTokenResult;
import org.derleta.authorization.security.model.UserSecurity;
import org.derleta.authorization.security.util.EncryptionUtil;
import org.derleta.authorization.security.util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

/**
 * Service for managing RefreshToken entities. Provides functionality for
 * retrieving, saving, deleting, and managing refresh tokens with associated filters
 * and pagination support.
 */
@Service
public class RefreshTokenService extends AbstractTokenService<RefreshToken> {

    private final RefreshTokenRepository repository;

    private final Counter refreshTokenCreated;
    private final Counter refreshTokenCreateFailed;
    private final Timer refreshTokenCreateTimer;

    @Autowired
    public RefreshTokenService(RefreshTokenRepository repository, UserRepository userRepository,
                               EncryptionUtil encryptionUtil,
                               UserSecurityService userSecurityService,
                               JwtTokenUtil jwtTokenUtil,
                               MeterRegistry meterRegistry
    ) {
        super(userRepository, encryptionUtil, userSecurityService, jwtTokenUtil);
        this.repository = repository;

        this.refreshTokenCreated = meterRegistry.counter("auth_token_created_total", "type", "refresh");
        this.refreshTokenCreateFailed = meterRegistry.counter("auth_token_create_failed_total", "type", "refresh");
        this.refreshTokenCreateTimer = meterRegistry.timer("auth_token_create_seconds", "type", "refresh");
    }

    @Override
    protected TokenRepositoryInterface repository() {
        return repository;
    }

    @Override
    protected List<RefreshToken> mapToModels(List<TokenEntity> entities) {
        return TokenApiMapper.toRefreshTokens(entities);
    }

    @Override
    protected RefreshToken mapToModel(TokenEntity entity) {
        return TokenApiMapper.toRefreshToken(entity);
    }

    @Override
    protected RefreshToken createForUser(final UserSecurity userSecurity) {
        return refreshTokenCreateTimer.record(() -> {
            if (userSecurity == null) {
                refreshTokenCreateFailed.increment();
                return null;
            }

            final long userId = userSecurity.getId();
            final RefreshTokenResult tokenResult = jwtTokenUtil.generateRefreshToken(userSecurity);
            final String tokenStr = tokenResult.token();
            final String encryptedToken = encryptionUtil.encrypt(tokenStr);
            final String jti = jwtTokenUtil.getJti(tokenStr);
            final Timestamp expiration = new Timestamp(tokenResult.expiresAt().getTime());

            final int rows = repository.save(
                    userId,
                    encryptedToken,
                    expiration,
                    userSecurity.getTokenVersion(),
                    jti,
                    jwtTokenUtil.isRevokedClaim(tokenStr)
            );

            return afterSaveMapByJti(rows, jti, refreshTokenCreateFailed, refreshTokenCreated);
        });
    }
}
