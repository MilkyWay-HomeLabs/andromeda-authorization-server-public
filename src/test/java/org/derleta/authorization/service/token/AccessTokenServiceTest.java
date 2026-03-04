package org.derleta.authorization.service.token;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.derleta.authorization.domain.entity.token.AccessTokenEntity;
import org.derleta.authorization.domain.model.AccessToken;
import org.derleta.authorization.repository.impl.UserRepository;
import org.derleta.authorization.repository.impl.token.AccessTokenRepository;
import org.derleta.authorization.security.api.UserSecurityService;
import org.derleta.authorization.security.model.UserSecurity;
import org.derleta.authorization.security.util.EncryptionUtil;
import org.derleta.authorization.security.util.JwtTokenUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccessTokenServiceTest {

    @Mock
    private AccessTokenRepository repository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private EncryptionUtil encryptionUtil;
    @Mock
    private UserSecurityService userSecurityService;
    @Mock
    private JwtTokenUtil jwtTokenUtil;

    private MeterRegistry meterRegistry;
    private AccessTokenService service;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        service = new AccessTokenService(
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

        String token = "raw-token";
        String encryptedToken = "encrypted-token";
        String jti = "jti-123";
        Date expirationDate = new Date();

        when(jwtTokenUtil.generateAccessToken(userSecurity)).thenReturn(token);
        when(jwtTokenUtil.getJti(token)).thenReturn(jti);
        when(encryptionUtil.encrypt(token)).thenReturn(encryptedToken);
        when(jwtTokenUtil.getTokenExpiration(token)).thenReturn(expirationDate);
        when(jwtTokenUtil.isRevokedClaim(token)).thenReturn(false);

        when(repository.save(eq(userId), eq(encryptedToken), any(Timestamp.class), eq(1), eq(jti), eq(false))).thenReturn(1);

        AccessTokenEntity entity = new AccessTokenEntity();
        entity.setToken(encryptedToken);
        entity.setJti(jti);
        when(repository.findByJti(jti)).thenReturn(Optional.of(entity));
        when(encryptionUtil.decrypt(encryptedToken)).thenReturn(token);

        AccessToken result = service.createForUser(userId);

        assertNotNull(result);
        assertEquals(token, result.token());
        assertEquals(jti, result.jti());

        verify(repository).save(eq(userId), eq(encryptedToken), any(Timestamp.class), eq(1), eq(jti), eq(false));
        assertEquals(1, meterRegistry.counter("auth_token_created_total", "type", "access").count());
    }

    @Test
    void testCreateForUser_InvalidId() {
        assertNull(service.createForUser(0L));
        assertNull(service.createForUser(-1L));
        verifyNoInteractions(userSecurityService);
    }

    @Test
    void testCreateForUser_UserNotFound() {
        long userId = 1L;
        when(userRepository.isValidId(userId)).thenReturn(true);
        when(userSecurityService.loadUserSecurity(userId)).thenReturn(null);

        assertNull(service.createForUser(userId));
    }

    @Test
    void testCreateForUser_SaveFailure() {
        long userId = 1L;
        UserSecurity userSecurity = mock(UserSecurity.class);
        when(userSecurity.getId()).thenReturn(userId);

        when(userRepository.isValidId(userId)).thenReturn(true);
        when(userSecurityService.loadUserSecurity(userId)).thenReturn(userSecurity);

        String token = "raw-token";
        when(jwtTokenUtil.generateAccessToken(userSecurity)).thenReturn(token);
        when(jwtTokenUtil.getJti(token)).thenReturn("jti");
        when(encryptionUtil.encrypt(token)).thenReturn("enc");
        when(jwtTokenUtil.getTokenExpiration(token)).thenReturn(new Date());

        when(repository.save(anyLong(), anyString(), any(), anyInt(), anyString(), anyBoolean())).thenReturn(0);

        AccessToken result = service.createForUser(userId);

        assertNull(result);
        assertEquals(1, meterRegistry.counter("auth_token_create_failed_total", "type", "access").count());
    }

    @Test
    void testGetPage() {
        when(repository.getSortedPageWithFilters(anyInt(), anyInt(), anyString(), anyString(), anyString(), anyString()))
                .thenReturn(Collections.singletonList(new AccessTokenEntity()));
        when(repository.getFiltersCount(anyString(), anyString())).thenReturn(1L);
        when(encryptionUtil.decrypt(any())).thenReturn("decrypted");

        Page<AccessToken> page = service.getPage(0, 10, "username", "asc", "user", "email");

        assertNotNull(page);
        assertEquals(1, page.getTotalElements());
        verify(repository).getSortedPageWithFilters(0, 10, "u.username", "ASC", "user", "email");
    }

    @Test
    void testGetValid() {
        when(repository.findValid(anyInt(), anyInt(), anyString(), anyString()))
                .thenReturn(Collections.singletonList(new AccessTokenEntity()));
        when(repository.getValidCount()).thenReturn(1L);
        when(encryptionUtil.decrypt(any())).thenReturn("decrypted");

        Page<AccessToken> page = service.getValid(0, 10, "email", "desc");

        assertNotNull(page);
        assertEquals(1, page.getTotalElements());
        verify(repository).findValid(0, 10, "u.email", "DESC");
    }

    @Test
    void testGet_Success() {
        long tokenId = 1L;
        AccessTokenEntity entity = new AccessTokenEntity();
        entity.setToken("encrypted");
        when(repository.findById(tokenId)).thenReturn(Optional.of(entity));
        when(encryptionUtil.decrypt("encrypted")).thenReturn("decrypted");

        AccessToken result = service.get(tokenId);

        assertNotNull(result);
        assertEquals("decrypted", result.token());
    }

    @Test
    void testGet_NotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());
        assertNull(service.get(1L));
    }

    @Test
    void testDelete_Success() {
        long tokenId = 1L;
        long userId = 2L;
        AccessTokenEntity entity = new AccessTokenEntity();
        entity.setTokenId(tokenId);
        when(repository.findById(tokenId)).thenReturn(Optional.of(entity));

        assertTrue(service.delete(tokenId, userId));
        verify(repository).deleteById(tokenId, userId);
    }

    @Test
    void testDelete_NotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());
        assertFalse(service.delete(1L, 2L));
    }
}
