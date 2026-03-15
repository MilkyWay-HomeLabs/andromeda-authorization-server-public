package org.derleta.authorization.security.api;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import jakarta.servlet.http.HttpServletRequest;
import org.derleta.authorization.domain.entity.UserEntity;
import org.derleta.authorization.exception.ObjectNotSavedException;
import org.derleta.authorization.repository.impl.UserRepository;
import org.derleta.authorization.repository.impl.UserRolesRepository;
import org.derleta.authorization.repository.impl.token.AccessTokenRepository;
import org.derleta.authorization.repository.impl.token.RefreshTokenRepository;
import org.derleta.authorization.security.model.RefreshTokenResult;
import org.derleta.authorization.security.model.TokenPair;
import org.derleta.authorization.security.model.UserSecurity;
import org.derleta.authorization.security.store.RefreshTokenIncidentStore;
import org.derleta.authorization.security.util.EncryptionUtil;
import org.derleta.authorization.security.util.JwtTokenUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;

import java.util.Collections;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthApiServiceTest {

    private MeterRegistry meterRegistry;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private AccessTokenRepository accessTokenRepository;

    @Mock
    private JwtTokenUtil jwtTokenUtil;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserRolesRepository userRolesRepository;

    @Mock
    private UserSecurityService userSecurityService;

    @Mock
    private EncryptionUtil encryptionUtil;

    @Mock
    private RefreshTokenIncidentStore refreshTokenIncidentStore;

    private AuthApiService authApiService;

    private MockedStatic<Timer> timerMockedStatic;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();

        timerMockedStatic = mockStatic(Timer.class);
        Timer.Builder timerBuilder = mock(Timer.Builder.class, Answers.RETURNS_SELF);
        timerMockedStatic.when(() -> Timer.builder(anyString())).thenReturn(timerBuilder);
        when(timerBuilder.register(any(MeterRegistry.class))).thenReturn(mock(Timer.class));

        MockedStatic<Counter> counterMockedStatic = mockStatic(Counter.class);
        Counter.Builder counterBuilder = mock(Counter.Builder.class, Answers.RETURNS_SELF);
        counterMockedStatic.when(() -> Counter.builder(anyString())).thenReturn(counterBuilder);
        when(counterBuilder.register(any(MeterRegistry.class))).thenReturn(mock(Counter.class));

        timerMockedStatic.when(() -> Timer.start(any(MeterRegistry.class))).thenReturn(mock(Timer.Sample.class));

        authApiService = new AuthApiService(meterRegistry);
        authApiService.setRepository(
                refreshTokenRepository,
                accessTokenRepository,
                jwtTokenUtil,
                userRepository,
                userRolesRepository,
                userSecurityService,
                encryptionUtil,
                refreshTokenIncidentStore
        );

        counterMockedStatic.close();
    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
        timerMockedStatic.close();
    }

    @Test
    void testIssueTokenPairForLogin_Success() {
        UserSecurity userSecurity = mock(UserSecurity.class);
        when(userSecurity.getId()).thenReturn(1L);
        when(jwtTokenUtil.generateAccessToken(userSecurity)).thenReturn("accessToken");
        RefreshTokenResult refreshResult = new RefreshTokenResult("refreshToken", "jti", new Date(), new Date());
        when(jwtTokenUtil.generateRefreshToken(userSecurity)).thenReturn(refreshResult);
        when(encryptionUtil.encrypt(anyString())).thenAnswer(i -> i.getArgument(0));
        when(accessTokenRepository.save(anyLong(), anyString(), any(), anyInt(), any(), anyBoolean())).thenReturn(1);
        when(refreshTokenRepository.save(anyLong(), anyString(), any(), anyInt(), any(), anyBoolean())).thenReturn(1);

        TokenPair result = authApiService.issueTokenPairForLogin(userSecurity);

        assertNotNull(result);
        assertEquals("accessToken", result.accessToken());
        assertEquals("refreshToken", result.refreshToken());
        verify(accessTokenRepository).save(eq(1L), eq("accessToken"), any(), anyInt(), any(), eq(false));
        verify(refreshTokenRepository).save(eq(1L), eq("refreshToken"), any(), anyInt(), any(), eq(false));
    }

    @Test
    void testUpdateAccessToken_Success() {
        Long userId = 1L;
        UserEntity userEntity = new UserEntity();
        userEntity.setUserId(userId);
        when(userRepository.findById(userId)).thenReturn(userEntity);
        when(userRolesRepository.getRoles(userId)).thenReturn(Collections.emptyList());
        when(jwtTokenUtil.generateAccessToken(any())).thenReturn("newAccessToken");
        when(encryptionUtil.encrypt("newAccessToken")).thenReturn("encAccessToken");
        when(accessTokenRepository.save(anyLong(), eq("encAccessToken"), any(), anyInt(), any(), anyBoolean())).thenReturn(1);

        String result = authApiService.updateAccessToken(userId);

        assertEquals("newAccessToken", result);
        verify(accessTokenRepository).save(eq(userId), eq("encAccessToken"), any(), anyInt(), any(), eq(false));
    }

    @Test
    void testUpdateAccessToken_UserNotFound() {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(null);

        assertThrows(RuntimeException.class, () -> authApiService.updateAccessToken(userId));
    }

    @Test
    void testRefresh_Success() {
        String refreshToken = "validRefreshToken";
        HttpServletRequest request = mock(HttpServletRequest.class);

        when(jwtTokenUtil.getJti(refreshToken)).thenReturn("jti");
        when(jwtTokenUtil.getUserId(refreshToken)).thenReturn(1L);
        when(jwtTokenUtil.getTokenVersion(refreshToken)).thenReturn(1);
        when(refreshTokenRepository.isActive("jti", 1L, 1)).thenReturn(true);
        when(refreshTokenRepository.revokeByJti("jti", 1L, 1)).thenReturn(true);

        UserSecurity userSecurity = mock(UserSecurity.class);
        when(userSecurity.getId()).thenReturn(1L);
        when(userSecurityService.loadUserSecurity(1L)).thenReturn(userSecurity);

        Date sessionExp = new Date();
        when(jwtTokenUtil.getSessionExpiration(refreshToken)).thenReturn(sessionExp);

        RefreshTokenResult newRefresh = new RefreshTokenResult("newRefreshToken", "newJti", new Date(), sessionExp);
        when(jwtTokenUtil.generateRefreshToken(userSecurity, sessionExp)).thenReturn(newRefresh);

        // Mock for updateAccessToken inside refresh
        UserEntity userEntity = new UserEntity();
        userEntity.setUserId(1L);
        when(userRepository.findById(1L)).thenReturn(userEntity);
        when(userRolesRepository.getRoles(1L)).thenReturn(Collections.emptyList());
        when(jwtTokenUtil.generateAccessToken(any())).thenReturn("newAccessToken");

        when(encryptionUtil.encrypt(anyString())).thenAnswer(i -> "enc_" + i.getArgument(0));
        when(refreshTokenRepository.save(anyLong(), anyString(), any(), anyInt(), any(), anyBoolean())).thenReturn(1);
        when(accessTokenRepository.save(anyLong(), anyString(), any(), anyInt(), any(), anyBoolean())).thenReturn(1);

        TokenPair result = authApiService.refresh(refreshToken, request);

        assertNotNull(result);
        assertEquals("newAccessToken", result.accessToken());
        assertEquals("newRefreshToken", result.refreshToken());
    }

    @Test
    void testRefresh_AlreadyRevoked() {
        String refreshToken = "revokedToken";
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");

        when(jwtTokenUtil.getJti(refreshToken)).thenReturn("jti");
        when(jwtTokenUtil.getUserId(refreshToken)).thenReturn(1L);
        when(jwtTokenUtil.getTokenVersion(refreshToken)).thenReturn(1);
        when(refreshTokenRepository.isActive("jti", 1L, 1)).thenReturn(true);
        when(refreshTokenRepository.revokeByJti("jti", 1L, 1)).thenReturn(false);

        assertThrows(BadCredentialsException.class, () -> authApiService.refresh(refreshToken, request));
        verify(refreshTokenIncidentStore).save(argThat(incident ->
                incident.getDescription().contains("replay") && "127.0.0.1".equals(incident.getIpAddress())
        ));
    }

    @Test
    void testRefresh_MissingSessionExpiration() {
        String refreshToken = "tokenNoExp";
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(jwtTokenUtil.getJti(refreshToken)).thenReturn("jti");
        when(jwtTokenUtil.getUserId(refreshToken)).thenReturn(1L);
        when(jwtTokenUtil.getTokenVersion(refreshToken)).thenReturn(1);
        when(refreshTokenRepository.isActive("jti", 1L, 1)).thenReturn(true);
        when(refreshTokenRepository.revokeByJti("jti", 1L, 1)).thenReturn(true);
        when(jwtTokenUtil.getSessionExpiration(refreshToken)).thenReturn(null);

        assertThrows(BadCredentialsException.class, () -> authApiService.refresh(refreshToken, request));
    }

    @Test
    void testUpdateAccessToken_NullUserId() {
        assertThrows(IllegalArgumentException.class, () -> authApiService.updateAccessToken(null));
    }

    @Test
    void testLogout_BlankToken() {
        assertThrows(BadCredentialsException.class, () -> authApiService.logout(" "));
    }

    @Test
    void testLogoutAll_NullUserId() {
        assertThrows(BadCredentialsException.class, () -> authApiService.logoutAll(null));
    }

    @Test
    void testIssueTokenPairForLogin_NullUser() {
        assertThrows(IllegalArgumentException.class, () -> authApiService.issueTokenPairForLogin(null));
    }

    @Test
    void testRefresh_InactiveToken() {
        String refreshToken = "inactiveToken";
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(jwtTokenUtil.getJti(refreshToken)).thenReturn("jti");
        when(jwtTokenUtil.getUserId(refreshToken)).thenReturn(1L);
        when(jwtTokenUtil.getTokenVersion(refreshToken)).thenReturn(1);
        when(refreshTokenRepository.isActive("jti", 1L, 1)).thenReturn(false);

        assertThrows(BadCredentialsException.class, () -> authApiService.refresh(refreshToken, request));
        verify(refreshTokenIncidentStore).save(any());
    }

    @Test
    void testLogout_Success() {
        String refreshToken = "validToken";
        when(jwtTokenUtil.getJti(refreshToken)).thenReturn("jti");
        when(jwtTokenUtil.getUserId(refreshToken)).thenReturn(1L);
        when(jwtTokenUtil.getTokenVersion(refreshToken)).thenReturn(1);

        authApiService.logout(refreshToken);

        verify(refreshTokenRepository).revokeByJti("jti", 1L, 1);
    }

    @Test
    void testLogoutAll_Success() {
        Long userId = 1L;
        when(userRepository.incrementTokenVersion(userId)).thenReturn(1);
        when(refreshTokenRepository.revokeAllByUserId(userId)).thenReturn(5);
        when(accessTokenRepository.revokeAllByUserId(userId)).thenReturn(10);

        authApiService.logoutAll(userId);

        verify(userRepository).incrementTokenVersion(userId);
        verify(refreshTokenRepository).revokeAllByUserId(userId);
        verify(accessTokenRepository).revokeAllByUserId(userId);
    }

    @Test
    void testSaveTokenInternal_ThrowsObjectNotSavedException() {
        UserSecurity userSecurity = mock(UserSecurity.class);
        when(userSecurity.getId()).thenReturn(1L);
        when(jwtTokenUtil.generateAccessToken(userSecurity)).thenReturn("accessToken");
        RefreshTokenResult refreshResult = new RefreshTokenResult("refreshToken", "jti", new Date(), new Date());
        when(jwtTokenUtil.generateRefreshToken(userSecurity)).thenReturn(refreshResult);

        when(encryptionUtil.encrypt(anyString())).thenAnswer(i -> i.getArgument(0));
        when(accessTokenRepository.save(anyLong(), anyString(), any(), anyInt(), any(), anyBoolean())).thenReturn(0);

        assertThrows(ObjectNotSavedException.class, () -> authApiService.issueTokenPairForLogin(userSecurity));
    }
}
