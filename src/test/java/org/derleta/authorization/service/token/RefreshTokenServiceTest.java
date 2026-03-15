package org.derleta.authorization.service.token;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.derleta.authorization.domain.entity.token.RefreshTokenEntity;
import org.derleta.authorization.domain.model.RefreshToken;
import org.derleta.authorization.repository.impl.UserRepository;
import org.derleta.authorization.repository.impl.token.RefreshTokenRepository;
import org.derleta.authorization.security.api.UserSecurityService;
import org.derleta.authorization.security.model.RefreshTokenResult;
import org.derleta.authorization.security.model.UserSecurity;
import org.derleta.authorization.security.util.EncryptionUtil;
import org.derleta.authorization.security.util.JwtTokenUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository repository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private EncryptionUtil encryptionUtil;
    @Mock
    private UserSecurityService userSecurityService;
    @Mock
    private JwtTokenUtil jwtTokenUtil;

    private MeterRegistry meterRegistry;
    private RefreshTokenService service;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        service = new RefreshTokenService(
                repository,
                userRepository,
                encryptionUtil,
                userSecurityService,
                jwtTokenUtil,
                meterRegistry
        );
    }

    @Test
    void testCreateForUser_Success() {
        long userId = 1L;
        UserSecurity userSecurity = mock(UserSecurity.class);
        when(userSecurity.getId()).thenReturn(userId);
        when(userSecurity.getTokenVersion()).thenReturn(1);

        when(userRepository.isValidId(userId)).thenReturn(true);
        when(userSecurityService.loadUserSecurity(userId)).thenReturn(userSecurity);

        String token = "raw-refresh-token";
        String encryptedToken = "encrypted-refresh-token";
        String jti = "refresh-jti-123";
        Date expirationDate = new Date();
        RefreshTokenResult tokenResult = new RefreshTokenResult(token, jti, expirationDate, expirationDate);

        when(jwtTokenUtil.generateRefreshToken(userSecurity)).thenReturn(tokenResult);
        when(encryptionUtil.encrypt(token)).thenReturn(encryptedToken);
        when(jwtTokenUtil.getJti(token)).thenReturn(jti);
        when(jwtTokenUtil.isRevokedClaim(token)).thenReturn(false);

        when(repository.save(eq(userId), eq(encryptedToken), any(Timestamp.class), eq(1), eq(jti), eq(false))).thenReturn(1);

        RefreshTokenEntity entity = new RefreshTokenEntity();
        entity.setToken(encryptedToken);
        entity.setJti(jti);
        when(repository.findByJti(jti)).thenReturn(Optional.of(entity));
        when(encryptionUtil.decrypt(encryptedToken)).thenReturn(token);

        RefreshToken result = service.createForUser(userId);

        assertNotNull(result);
        assertEquals(token, result.token());
        assertEquals(jti, result.jti());

        verify(repository).save(eq(userId), eq(encryptedToken), any(Timestamp.class), eq(1), eq(jti), eq(false));
        assertEquals(1, meterRegistry.counter("auth_token_created_total", "type", "refresh").count());
    }

    @Test
    void testCreateForUser_SaveFailure() {
        long userId = 1L;
        UserSecurity userSecurity = mock(UserSecurity.class);
        when(userSecurity.getId()).thenReturn(userId);

        when(userRepository.isValidId(userId)).thenReturn(true);
        when(userSecurityService.loadUserSecurity(userId)).thenReturn(userSecurity);

        RefreshTokenResult tokenResult = new RefreshTokenResult("t", "j", new Date(), new Date());
        when(jwtTokenUtil.generateRefreshToken(any())).thenReturn(tokenResult);
        when(encryptionUtil.encrypt(anyString())).thenReturn("e");

        when(repository.save(anyLong(), anyString(), any(), anyInt(), any(), anyBoolean())).thenReturn(0);

        RefreshToken result = service.createForUser(userId);

        assertNull(result);
        assertEquals(1, meterRegistry.counter("auth_token_create_failed_total", "type", "refresh").count());
    }
}
