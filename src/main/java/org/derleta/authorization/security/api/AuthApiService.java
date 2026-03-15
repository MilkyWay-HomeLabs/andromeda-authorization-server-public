package org.derleta.authorization.security.api;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import jakarta.servlet.http.HttpServletRequest;
import org.derleta.authorization.domain.builder.impl.RefreshTokenIncidentBuilderImpl;
import org.derleta.authorization.domain.entity.RoleEntity;
import org.derleta.authorization.domain.entity.UserEntity;
import org.derleta.authorization.domain.entity.token.RefreshTokenIncidentEntity;
import org.derleta.authorization.exception.ObjectNotSavedException;
import org.derleta.authorization.repository.impl.UserRepository;
import org.derleta.authorization.repository.impl.UserRolesRepository;
import org.derleta.authorization.repository.impl.token.AccessTokenRepository;
import org.derleta.authorization.repository.impl.token.RefreshTokenRepository;
import org.derleta.authorization.security.TokenSaver;
import org.derleta.authorization.security.mapper.UserSecurityMapper;
import org.derleta.authorization.security.model.RefreshTokenResult;
import org.derleta.authorization.security.model.TokenPair;
import org.derleta.authorization.security.model.UserSecurity;
import org.derleta.authorization.security.store.RefreshTokenIncidentStore;
import org.derleta.authorization.security.util.EncryptionUtil;
import org.derleta.authorization.security.util.JwtTokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class AuthApiService {

    private static final Logger log = LoggerFactory.getLogger(AuthApiService.class);

    private JwtTokenUtil jwtTokenUtil;
    private UserRepository userRepository;
    private UserRolesRepository userRolesRepository;
    private UserSecurityService userSecurityService;
    private RefreshTokenRepository refreshTokenRepository;
    private AccessTokenRepository accessTokenRepository;
    private EncryptionUtil encryptionUtil;
    private RefreshTokenIncidentStore refreshTokenIncidentStore;

    private final MeterRegistry meterRegistry;

    // Timers registered once
    private final Timer tUpdateAccessToken;
    private final Timer tRefresh;
    private final Timer tRefreshDbIsActive;
    private final Timer tRefreshDbRevokeByJti;
    private final Timer tRefreshGenerateRefresh;
    private final Timer tLogout;
    private final Timer tLogoutDbRevokeByJti;
    private final Timer tLogoutAll;
    private final Timer tLogoutAllDbIncrementTokenVersion;
    private final Timer tLogoutAllDbRevokeRefresh;
    private final Timer tLogoutAllDbRevokeAccess;
    private final Timer tTokenEncrypt;
    private final Timer tTokenDbSave;
    private final Timer tTokenSaveInternal;

    // Counters registered once
    private final Counter cUpdateAccessTokenSuccess;
    private final Counter cUpdateAccessTokenNullUserId;
    private final Counter cUpdateAccessTokenError;

    private final Counter cRefreshSuccess;
    private final Counter cRefreshBlank;
    private final Counter cRefreshInvalid;
    private final Counter cRefreshAlreadyUsed;
    private final Counter cRefreshMissingSessionExp;
    private final Counter cRefreshError;

    private final Counter cLogoutSuccess;
    private final Counter cLogoutBlank;
    private final Counter cLogoutError;

    private final Counter cLogoutAllSuccess;
    private final Counter cLogoutAllNullUserId;
    private final Counter cLogoutAllUserNotFound;
    private final Counter cLogoutAllError;

    private final Counter cTokenSavedSuccessAccess;
    private final Counter cTokenSavedSuccessRefresh;
    private final Counter cTokenSavedNotSavedAccess;
    private final Counter cTokenSavedNotSavedRefresh;
    private final Counter cTokenSavedErrorAccess;
    private final Counter cTokenSavedErrorRefresh;

    private final Counter cLogoutAllRevokedRefresh;
    private final Counter cLogoutAllRevokedAccess;

    @Autowired
    public AuthApiService(final MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;

        this.tUpdateAccessToken = Timer.builder("auth_update_access_token_seconds").register(meterRegistry);
        this.tRefresh = Timer.builder("auth_refresh_seconds").register(meterRegistry);
        this.tRefreshDbIsActive = Timer.builder("auth_refresh_db_is_active_seconds").register(meterRegistry);
        this.tRefreshDbRevokeByJti = Timer.builder("auth_refresh_db_revoke_by_jti_seconds").register(meterRegistry);
        this.tRefreshGenerateRefresh = Timer.builder("auth_refresh_generate_refresh_seconds").register(meterRegistry);
        this.tLogout = Timer.builder("auth_logout_seconds").register(meterRegistry);
        this.tLogoutDbRevokeByJti = Timer.builder("auth_logout_db_revoke_by_jti_seconds").register(meterRegistry);
        this.tLogoutAll = Timer.builder("auth_logout_all_seconds").register(meterRegistry);
        this.tLogoutAllDbIncrementTokenVersion = Timer.builder("auth_logout_all_db_increment_token_version_seconds").register(meterRegistry);
        this.tLogoutAllDbRevokeRefresh = Timer.builder("auth_logout_all_db_revoke_refresh_seconds").register(meterRegistry);
        this.tLogoutAllDbRevokeAccess = Timer.builder("auth_logout_all_db_revoke_access_seconds").register(meterRegistry);
        this.tTokenEncrypt = Timer.builder("auth_token_encrypt_seconds").register(meterRegistry);
        this.tTokenDbSave = Timer.builder("auth_token_db_save_seconds").register(meterRegistry);
        this.tTokenSaveInternal = Timer.builder("auth_token_save_internal_seconds").register(meterRegistry);

        this.cUpdateAccessTokenSuccess = Counter.builder("auth_update_access_token_total").tag("outcome", "success").register(meterRegistry);
        this.cUpdateAccessTokenNullUserId = Counter.builder("auth_update_access_token_total").tag("outcome", "null_user_id").register(meterRegistry);
        this.cUpdateAccessTokenError = Counter.builder("auth_update_access_token_total").tag("outcome", "error").register(meterRegistry);

        this.cRefreshSuccess = Counter.builder("auth_refresh_total").tag("outcome", "success").register(meterRegistry);
        this.cRefreshBlank = Counter.builder("auth_refresh_total").tag("outcome", "blank").register(meterRegistry);
        this.cRefreshInvalid = Counter.builder("auth_refresh_total").tag("outcome", "invalid").register(meterRegistry);
        this.cRefreshAlreadyUsed = Counter.builder("auth_refresh_total").tag("outcome", "already_used").register(meterRegistry);
        this.cRefreshMissingSessionExp = Counter.builder("auth_refresh_total").tag("outcome", "missing_session_exp").register(meterRegistry);
        this.cRefreshError = Counter.builder("auth_refresh_total").tag("outcome", "error").register(meterRegistry);

        this.cLogoutSuccess = Counter.builder("auth_logout_total").tag("outcome", "success").register(meterRegistry);
        this.cLogoutBlank = Counter.builder("auth_logout_total").tag("outcome", "blank").register(meterRegistry);
        this.cLogoutError = Counter.builder("auth_logout_total").tag("outcome", "error").register(meterRegistry);

        this.cLogoutAllSuccess = Counter.builder("auth_logout_all_total").tag("outcome", "success").register(meterRegistry);
        this.cLogoutAllNullUserId = Counter.builder("auth_logout_all_total").tag("outcome", "null_user_id").register(meterRegistry);
        this.cLogoutAllUserNotFound = Counter.builder("auth_logout_all_total").tag("outcome", "user_not_found").register(meterRegistry);
        this.cLogoutAllError = Counter.builder("auth_logout_all_total").tag("outcome", "error").register(meterRegistry);

        this.cTokenSavedSuccessAccess =
                Counter.builder("auth_token_saved_total").tag("outcome", "success").tag("token_type", "access").register(meterRegistry);
        this.cTokenSavedSuccessRefresh =
                Counter.builder("auth_token_saved_total").tag("outcome", "success").tag("token_type", "refresh").register(meterRegistry);

        this.cTokenSavedNotSavedAccess =
                Counter.builder("auth_token_saved_total").tag("outcome", "not_saved").tag("token_type", "access").register(meterRegistry);
        this.cTokenSavedNotSavedRefresh =
                Counter.builder("auth_token_saved_total").tag("outcome", "not_saved").tag("token_type", "refresh").register(meterRegistry);

        this.cTokenSavedErrorAccess =
                Counter.builder("auth_token_saved_total").tag("outcome", "error").tag("token_type", "access").register(meterRegistry);
        this.cTokenSavedErrorRefresh =
                Counter.builder("auth_token_saved_total").tag("outcome", "error").tag("token_type", "refresh").register(meterRegistry);

        this.cLogoutAllRevokedRefresh = Counter.builder("auth_logout_all_revoked_total").tag("token_type", "refresh").register(meterRegistry);
        this.cLogoutAllRevokedAccess = Counter.builder("auth_logout_all_revoked_total").tag("token_type", "access").register(meterRegistry);
    }

    @Autowired
    public void setRepository(
            RefreshTokenRepository refreshTokenRepository,
            AccessTokenRepository accessTokenRepository,
            JwtTokenUtil jwtTokenUtil,
            UserRepository userRepository,
            UserRolesRepository userRolesRepository,
            UserSecurityService userSecurityService,
            EncryptionUtil encryptionUtil,
            RefreshTokenIncidentStore refreshTokenIncidentStore
    ) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.accessTokenRepository = accessTokenRepository;
        this.jwtTokenUtil = jwtTokenUtil;
        this.userRepository = userRepository;
        this.userRolesRepository = userRolesRepository;
        this.userSecurityService = userSecurityService;
        this.encryptionUtil = encryptionUtil;
        this.refreshTokenIncidentStore = refreshTokenIncidentStore;
    }

    @Transactional
    public TokenPair issueTokenPairForLogin(final UserSecurity userSecurity) {
        if (userSecurity == null) {
            throw new IllegalArgumentException("User is null");
        }

        final long userId = userSecurity.getId();

        final String accessToken = jwtTokenUtil.generateAccessToken(userSecurity);
        final RefreshTokenResult refresh = jwtTokenUtil.generateRefreshToken(userSecurity);

        saveAccessTokenInternal(userId, accessToken);
        saveRefreshTokenInternal(userId, refresh.token());

        return new TokenPair(accessToken, refresh.token());
    }

    public String updateAccessToken(final Long userId) {
        final Timer.Sample sample = Timer.start(meterRegistry);
        try {
            if (userId == null) {
                cUpdateAccessTokenNullUserId.increment();
                throw new IllegalArgumentException("UserId is null");
            }

            UserEntity userEntity = userRepository.findById(userId);
            if (userEntity == null) {
                throw new RuntimeException("User not found");
            }

            List<RoleEntity> roles = userRolesRepository.getRoles(userId);
            UserSecurity userSecurity = UserSecurityMapper.toUserSecurity(userEntity, new HashSet<>(roles));

            String accessToken = jwtTokenUtil.generateAccessToken(userSecurity);
            saveAccessTokenInternal(userId, accessToken);

            cUpdateAccessTokenSuccess.increment();
            return accessToken;
        } catch (RuntimeException ex) {
            cUpdateAccessTokenError.increment();
            throw ex;
        } finally {
            sample.stop(tUpdateAccessToken);
        }
    }

    @Transactional
    public TokenPair refresh(final String refreshToken, final HttpServletRequest request) {
        final Timer.Sample sample = Timer.start(meterRegistry);

        String oldJti;
        Long userId;
        Integer ver;

        try {
            if (refreshToken == null || refreshToken.isBlank()) {
                cRefreshBlank.increment();
                log.warn("Refresh denied: missing refresh token");
                throw new BadCredentialsException("Missing refresh token");
            }

            oldJti = jwtTokenUtil.getJti(refreshToken);
            userId = jwtTokenUtil.getUserId(refreshToken);
            ver = jwtTokenUtil.getTokenVersion(refreshToken);

            boolean active;
            final Timer.Sample isActiveSample = Timer.start(meterRegistry);
            try {
                active = refreshTokenRepository.isActive(oldJti, userId, ver);
            } finally {
                isActiveSample.stop(tRefreshDbIsActive);
            }

            if (!active) {
                cRefreshInvalid.increment();
                log.warn("Refresh denied: inactive/invalid token (userId={}, ver={}, jti={})", userId, ver, oldJti);

                recordIncidentSafe(new RefreshTokenIncidentBuilderImpl()
                        .userId(userId)
                        .jti(oldJti)
                        .version(ver)
                        .incidentTime(new Timestamp(System.currentTimeMillis()))
                        .ipAddress(extractClientIp(request))
                        .userAgent(extractUserAgent(request))
                        .description("Refresh denied: inactive/invalid refresh token")
                        .build());

                throw new BadCredentialsException("Invalid refresh token");
            }

            final boolean revoked;
            final Timer.Sample revokeSample = Timer.start(meterRegistry);
            try {
                revoked = refreshTokenRepository.revokeByJti(oldJti, userId, ver);
            } finally {
                revokeSample.stop(tRefreshDbRevokeByJti);
            }

            if (!revoked) {
                cRefreshAlreadyUsed.increment();
                log.warn("Refresh denied: token already used/revoked (userId={}, ver={}, jti={})", userId, ver, oldJti);

                recordIncidentSafe(new RefreshTokenIncidentBuilderImpl()
                        .userId(userId)
                        .jti(oldJti)
                        .version(ver)
                        .incidentTime(new Timestamp(System.currentTimeMillis()))
                        .ipAddress(extractClientIp(request))
                        .userAgent(extractUserAgent(request))
                        .description("Refresh denied: refresh token replay (already used/revoked)")
                        .build());

                throw new BadCredentialsException("Refresh token already used");
            }

            UserSecurity userSecurity = userSecurityService.loadUserSecurity(userId);

            final java.util.Date sessionExp = jwtTokenUtil.getSessionExpiration(refreshToken);
            if (sessionExp == null) {
                cRefreshMissingSessionExp.increment();
                log.warn("Refresh denied: missing session expiration (userId={}, ver={}, jti={})", userId, ver, oldJti);
                throw new BadCredentialsException("Missing session expiration");
            }

            final RefreshTokenResult newRefresh;
            final Timer.Sample genSample = Timer.start(meterRegistry);
            try {
                newRefresh = jwtTokenUtil.generateRefreshToken(userSecurity, sessionExp);
            } finally {
                genSample.stop(tRefreshGenerateRefresh);
            }

            saveRefreshTokenInternal(userSecurity.getId(), newRefresh.token());
            String newAccess = updateAccessToken(userId);

            cRefreshSuccess.increment();
            return new TokenPair(newAccess, newRefresh.token());
        } catch (RuntimeException ex) {
            cRefreshError.increment();
            log.error("Refresh error", ex);
            throw ex;
        } finally {
            sample.stop(tRefresh);
        }
    }

    public void logout(final String refreshToken) {
        final Timer.Sample sample = Timer.start(meterRegistry);
        try {
            if (refreshToken == null || refreshToken.isBlank()) {
                cLogoutBlank.increment();
                throw new BadCredentialsException("Missing refresh token");
            }

            final String jti = jwtTokenUtil.getJti(refreshToken);
            final Long userId = jwtTokenUtil.getUserId(refreshToken);
            final Integer ver = jwtTokenUtil.getTokenVersion(refreshToken);

            final Timer.Sample revokeSample = Timer.start(meterRegistry);
            try {
                refreshTokenRepository.revokeByJti(jti, userId, ver);
            } finally {
                revokeSample.stop(tLogoutDbRevokeByJti);
            }

            cLogoutSuccess.increment();
        } catch (RuntimeException ex) {
            cLogoutError.increment();
            throw ex;
        } finally {
            sample.stop(tLogout);
        }
    }

    @Transactional
    public void logoutAll(final Long userId) {
        final Timer.Sample sample = Timer.start(meterRegistry);
        try {
            if (userId == null) {
                cLogoutAllNullUserId.increment();
                throw new BadCredentialsException("Missing user id");
            }

            final int bumped;
            final Timer.Sample bumpSample = Timer.start(meterRegistry);
            try {
                bumped = userRepository.incrementTokenVersion(userId);
            } finally {
                bumpSample.stop(tLogoutAllDbIncrementTokenVersion);
            }

            if (bumped != 1) {
                cLogoutAllUserNotFound.increment();
                throw new RuntimeException("User not found or token version not updated");
            }

            final int revokedRefresh;
            final Timer.Sample revokeAllRefreshSample = Timer.start(meterRegistry);
            try {
                revokedRefresh = refreshTokenRepository.revokeAllByUserId(userId);
            } finally {
                revokeAllRefreshSample.stop(tLogoutAllDbRevokeRefresh);
            }

            final int revokedAccess;
            final Timer.Sample revokeAllAccessSample = Timer.start(meterRegistry);
            try {
                revokedAccess = accessTokenRepository.revokeAllByUserId(userId);
            } finally {
                revokeAllAccessSample.stop(tLogoutAllDbRevokeAccess);
            }

            cLogoutAllRevokedRefresh.increment(revokedRefresh);
            cLogoutAllRevokedAccess.increment(revokedAccess);

            cLogoutAllSuccess.increment();
        } catch (RuntimeException ex) {
            cLogoutAllError.increment();
            throw ex;
        } finally {
            sample.stop(tLogoutAll);
        }
    }

    private void saveRefreshTokenInternal(final long userId, final String token) {
        saveTokenInternal("refresh", userId, token, (uid, enc, exp, ver, jti, revoked) ->
                refreshTokenRepository.save(uid, enc, exp, ver, jti, revoked)
        );
    }

    private void saveAccessTokenInternal(final long userId, final String token) {
        saveTokenInternal("access", userId, token, (uid, enc, exp, ver, jti, revoked) ->
                accessTokenRepository.save(uid, enc, exp, ver, jti, revoked)
        );
    }

    private void saveTokenInternal(final String tokenType, final long userId, final String token, final TokenSaver saver) {
        final long startNs = System.nanoTime();
        try {
            final long encStartNs = System.nanoTime();
            final String encryptedToken = encryptionUtil.encrypt(token);
            tTokenEncrypt.record(System.nanoTime() - encStartNs, TimeUnit.NANOSECONDS);

            final java.util.Date exp = jwtTokenUtil.getTokenExpiration(token);
            final java.sql.Timestamp expirationTs = (exp == null) ? null : new java.sql.Timestamp(exp.getTime());

            final String jti = jwtTokenUtil.getJti(token);
            final int version = jwtTokenUtil.getTokenVersion(token);

            final long dbStartNs = System.nanoTime();
            final int result = saver.save(userId, encryptedToken, expirationTs, version, jti, false);
            tTokenDbSave.record(System.nanoTime() - dbStartNs, TimeUnit.NANOSECONDS);

            if (result <= 0) {
                if ("access".equals(tokenType)) cTokenSavedNotSavedAccess.increment();
                else cTokenSavedNotSavedRefresh.increment();
                throw new ObjectNotSavedException("Token not saved in database");
            }

            if ("access".equals(tokenType)) cTokenSavedSuccessAccess.increment();
            else cTokenSavedSuccessRefresh.increment();

        } catch (RuntimeException ex) {
            if ("access".equals(tokenType)) cTokenSavedErrorAccess.increment();
            else cTokenSavedErrorRefresh.increment();
            throw ex;
        } finally {
            tTokenSaveInternal.record(System.nanoTime() - startNs, TimeUnit.NANOSECONDS);
        }
    }

    private void recordIncidentSafe(final RefreshTokenIncidentEntity incident) {
        try {
            if (refreshTokenIncidentStore != null && incident != null) {
                refreshTokenIncidentStore.save(incident);
            }
        } catch (RuntimeException e) {
            log.error("Failed to persist refresh token incident", e);
        }
    }

    private String extractClientIp(final HttpServletRequest request) {
        if (request == null) return null;

        // if reverse proxy, typically real IP is in X-Forwarded-For
        final String xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            // the first address on the list is typically the client
            final int comma = xff.indexOf(',');
            return (comma > 0 ? xff.substring(0, comma) : xff).trim();
        }
        return request.getRemoteAddr();
    }

    private String extractUserAgent(final HttpServletRequest request) {
        return request == null ? null : request.getHeader("User-Agent");
    }

}
