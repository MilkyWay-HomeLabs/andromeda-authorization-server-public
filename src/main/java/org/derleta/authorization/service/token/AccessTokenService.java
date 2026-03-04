package org.derleta.authorization.service.token;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.derleta.authorization.controller.mapper.TokenApiMapper;
import org.derleta.authorization.domain.entity.token.TokenEntity;
import org.derleta.authorization.domain.model.AccessToken;
import org.derleta.authorization.repository.TokenRepositoryInterface;
import org.derleta.authorization.repository.impl.UserRepository;
import org.derleta.authorization.repository.impl.token.AccessTokenRepository;
import org.derleta.authorization.security.api.UserSecurityService;
import org.derleta.authorization.security.model.UserSecurity;
import org.derleta.authorization.security.util.EncryptionUtil;
import org.derleta.authorization.security.util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

/**
 * Service for managing access tokens. This class provides methods to retrieve,
 * save, and delete tokens, as well as to perform operations like filtering and
 * sorting token data.
 */
@Service
public class AccessTokenService extends AbstractTokenService<AccessToken> {

    private final AccessTokenRepository repository;

    private final Counter accessTokenCreated;
    private final Counter accessTokenCreateFailed;
    private final Timer accessTokenCreateTimer;

    @Autowired
    public AccessTokenService(
            AccessTokenRepository repository,
            UserRepository userRepository,
            EncryptionUtil encryptionUtil,
            UserSecurityService userSecurityService,
            JwtTokenUtil jwtTokenUtil,
            MeterRegistry meterRegistry
    ) {
        super(userRepository, encryptionUtil, userSecurityService, jwtTokenUtil);
        this.repository = repository;

        this.accessTokenCreated = meterRegistry.counter("auth_token_created_total", "type", "access");
        this.accessTokenCreateFailed = meterRegistry.counter("auth_token_create_failed_total", "type", "access");
        this.accessTokenCreateTimer = meterRegistry.timer("auth_token_create_seconds", "type", "access");
    }

    @Override
    protected TokenRepositoryInterface repository() {
        return repository;
    }

    @Override
    protected List<AccessToken> mapToModels(List<TokenEntity> entities) {
        return TokenApiMapper.toAccessTokens(entities);
    }

    @Override
    protected AccessToken mapToModel(TokenEntity entity) {
        return TokenApiMapper.toAccessToken(entity);
    }

    @Override
    protected AccessToken createForUser(final UserSecurity userSecurity) {
        return accessTokenCreateTimer.record(() -> {
            if (userSecurity == null) {
                accessTokenCreateFailed.increment();
                return null;
            }

            final String token = jwtTokenUtil.generateAccessToken(userSecurity);
            final String jti = jwtTokenUtil.getJti(token);
            final String encryptedToken = encryptionUtil.encrypt(token);
            final Timestamp expiration = new Timestamp(jwtTokenUtil.getTokenExpiration(token).getTime());

            final int rows = repository.save(
                    userSecurity.getId(),
                    encryptedToken,
                    expiration,
                    userSecurity.getTokenVersion(),
                    jti,
                    jwtTokenUtil.isRevokedClaim(token)
            );

            return afterSaveMapByJti(rows, jti, accessTokenCreateFailed, accessTokenCreated);
        });
    }

}
