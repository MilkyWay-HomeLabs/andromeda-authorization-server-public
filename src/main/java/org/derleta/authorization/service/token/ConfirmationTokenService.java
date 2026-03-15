package org.derleta.authorization.service.token;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.derleta.authorization.controller.mapper.TokenApiMapper;
import org.derleta.authorization.domain.entity.token.TokenEntity;
import org.derleta.authorization.domain.model.ConfirmationToken;
import org.derleta.authorization.domain.model.ConfirmationTokenResult;
import org.derleta.authorization.repository.TokenRepositoryInterface;
import org.derleta.authorization.repository.impl.UserRepository;
import org.derleta.authorization.repository.impl.token.ConfirmationTokenRepository;
import org.derleta.authorization.security.api.UserSecurityService;
import org.derleta.authorization.security.model.UserSecurity;
import org.derleta.authorization.security.util.EncryptionUtil;
import org.derleta.authorization.security.util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

/**
 * Service class for managing confirmation tokens.
 * Provides functionality for retrieving, saving, and deleting confirmation tokens,
 * as well as pagination and sorting capabilities.
 */
@Service
public class ConfirmationTokenService extends AbstractTokenService<ConfirmationToken> {

    private final ConfirmationTokenRepository repository;

    private final Counter confirmationTokenCreated;
    private final Counter confirmationTokenCreateFailed;
    private final Timer confirmationTokenCreateTimer;

    @Autowired
    public ConfirmationTokenService(
            ConfirmationTokenRepository repository,
            UserRepository userRepository,
            EncryptionUtil encryptionUtil,
            UserSecurityService userSecurityService,
            JwtTokenUtil jwtTokenUtil,
            MeterRegistry meterRegistry
    ) {
        super(userRepository, encryptionUtil, userSecurityService, jwtTokenUtil);
        this.repository = repository;

        this.confirmationTokenCreated = meterRegistry.counter("auth_token_created_total", "type", "confirm");
        this.confirmationTokenCreateFailed = meterRegistry.counter("auth_token_create_failed_total", "type", "confirm");
        this.confirmationTokenCreateTimer = meterRegistry.timer("auth_token_create_seconds", "type", "confirm");
    }

    @Override
    protected TokenRepositoryInterface repository() {
        return repository;
    }

    @Override
    protected List<ConfirmationToken> mapToModels(List<TokenEntity> entities) {
        return TokenApiMapper.toConfirmationTokens(entities);
    }

    @Override
    protected ConfirmationToken mapToModel(TokenEntity entity) {
        return TokenApiMapper.toConfirmationToken(entity);
    }

    @Override
    protected ConfirmationToken createForUser(final UserSecurity userSecurity) {
        return confirmationTokenCreateTimer.record(() -> {
            if (userSecurity == null) {
                confirmationTokenCreateFailed.increment();
                return null;
            }

            final long userId = userSecurity.getId();
            final int tokenVersion = userSecurity.getTokenVersion();
            final ConfirmationTokenResult tokenResult = jwtTokenUtil.generateConfirmationToken(userId, tokenVersion);
            final String encryptedToken = encryptionUtil.encrypt(tokenResult.token());
            final Timestamp expiration = new Timestamp(tokenResult.expiresAt().getTime());
            final String jti = tokenResult.jti();

            final int rows = repository.save(
                    userId,
                    encryptedToken,
                    expiration,
                    tokenVersion,
                    jti,
                    jwtTokenUtil.isRevokedClaim(tokenResult.token())
            );

            return afterSaveMapByJti(rows, jti, confirmationTokenCreateFailed, confirmationTokenCreated);
        });
    }

}
