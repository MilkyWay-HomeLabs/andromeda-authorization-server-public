package org.derleta.authorization.service.token;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.derleta.authorization.domain.entity.token.ConfirmationTokenEntity;
import org.derleta.authorization.domain.model.ConfirmationToken;
import org.derleta.authorization.domain.model.ConfirmationTokenResult;
import org.derleta.authorization.repository.impl.UserRepository;
import org.derleta.authorization.repository.impl.token.ConfirmationTokenRepository;
import org.derleta.authorization.security.api.UserSecurityService;
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
class ConfirmationTokenServiceTest {

    @Mock
    private ConfirmationTokenRepository repository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private EncryptionUtil encryptionUtil;
    @Mock
    private UserSecurityService userSecurityService;
    @Mock
    private JwtTokenUtil jwtTokenUtil;

    private MeterRegistry meterRegistry;
    private ConfirmationTokenService service;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        service = new ConfirmationTokenService(
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

        String token = "raw-confirm-token";
        String encryptedToken = "encrypted-confirm-token";
        String jti = "confirm-jti-123";
        Date expirationDate = new Date();
        ConfirmationTokenResult tokenResult = new ConfirmationTokenResult(token, jti, expirationDate, expirationDate);

        when(jwtTokenUtil.generateConfirmationToken(userId, 1)).thenReturn(tokenResult);
        when(encryptionUtil.encrypt(token)).thenReturn(encryptedToken);
        when(jwtTokenUtil.isRevokedClaim(token)).thenReturn(false);

        when(repository.save(eq(userId), eq(encryptedToken), any(Timestamp.class), eq(1), eq(jti), eq(false))).thenReturn(1);

        ConfirmationTokenEntity entity = new ConfirmationTokenEntity();
        entity.setToken(encryptedToken);
        entity.setJti(jti);
        when(repository.findByJti(jti)).thenReturn(Optional.of(entity));
        when(encryptionUtil.decrypt(encryptedToken)).thenReturn(token);

        ConfirmationToken result = service.createForUser(userId);

        assertNotNull(result);
        assertEquals(token, result.token());
        assertEquals(jti, result.jti());

        verify(repository).save(eq(userId), eq(encryptedToken), any(Timestamp.class), eq(1), eq(jti), eq(false));
        assertEquals(1, meterRegistry.counter("auth_token_created_total", "type", "confirm").count());
    }

    @Test
    void testCreateForUser_SaveFailure() {
        long userId = 1L;
        UserSecurity userSecurity = mock(UserSecurity.class);
        when(userSecurity.getId()).thenReturn(userId);
        when(userSecurity.getTokenVersion()).thenReturn(1);

        when(userRepository.isValidId(userId)).thenReturn(true);
        when(userSecurityService.loadUserSecurity(userId)).thenReturn(userSecurity);

        ConfirmationTokenResult tokenResult = new ConfirmationTokenResult("t", "j", new Date(), new Date());
        when(jwtTokenUtil.generateConfirmationToken(anyLong(), anyInt())).thenReturn(tokenResult);
        when(encryptionUtil.encrypt(anyString())).thenReturn("e");

        when(repository.save(anyLong(), anyString(), any(), anyInt(), anyString(), anyBoolean())).thenReturn(0);

        ConfirmationToken result = service.createForUser(userId);

        assertNull(result);
        assertEquals(1, meterRegistry.counter("auth_token_create_failed_total", "type", "confirm").count());
    }
}
