package org.derleta.authorization.controller;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.derleta.authorization.controller.dto.request.impl.AuthLoginRequest;
import org.derleta.authorization.controller.dto.response.AccessResponse;
import org.derleta.authorization.domain.types.AccessResponseType;
import org.derleta.authorization.security.api.AuthApiService;
import org.derleta.authorization.security.model.TokenPair;
import org.derleta.authorization.security.model.UserSecurity;
import org.derleta.authorization.security.util.JwtTokenUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    @Mock
    private AuthenticationManager authManager;

    @Mock
    private JwtTokenUtil jwtUtil;

    @Mock
    private AuthApiService authApiService;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private MeterRegistry meterRegistry;

    @Mock
    private Counter counter;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpServletRequest request;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // fixme: temporary solution, need to be replaced with proper configuration
        ReflectionTestUtils.setField(jwtUtil, "JWT_ACCESS_EXPIRATION", 3600);
        ReflectionTestUtils.setField(jwtUtil, "JWT_REFRESH_EXPIRATION", 1209600L);
    }

    @Test
    void testLogin_Success() {
        AuthLoginRequest loginRequest = new AuthLoginRequest("user", "pass");
        UserSecurity userSecurity = new UserSecurity(1L, "user", "email@test.com", "pass", null, null, true, false, 1, Collections.emptySet());
        TokenPair tokenPair = new TokenPair("access-token", "refresh-token");

        when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userSecurity);
        when(authApiService.issueTokenPairForLogin(userSecurity)).thenReturn(tokenPair);
        when(jwtUtil.getTokenExpiration(anyString())).thenReturn(new Date(System.currentTimeMillis() + 3600000));

        try (var mockedCounter = mockStatic(Counter.class)) {
            Counter.Builder builder = mock(Counter.Builder.class, Answers.RETURNS_SELF);
            mockedCounter.when(() -> Counter.builder(anyString())).thenReturn(builder);
            when(builder.register(any(MeterRegistry.class))).thenReturn(counter);

            ResponseEntity<?> result = controller.login(loginRequest, response);

            assertEquals(HttpStatus.OK, result.getStatusCode());
            assertNotNull(result.getBody());
            verify(response, times(2)).addHeader(eq("Set-Cookie"), anyString());
        }
    }

    @Test
    void testLogin_Failure() {
        AuthLoginRequest loginRequest = new AuthLoginRequest("user", "pass");
        when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenThrow(new BadCredentialsException("Invalid"));

        try (var mockedCounter = mockStatic(Counter.class)) {
            Counter.Builder builder = mock(Counter.Builder.class, Answers.RETURNS_SELF);
            mockedCounter.when(() -> Counter.builder(anyString())).thenReturn(builder);
            when(builder.register(any(MeterRegistry.class))).thenReturn(counter);

            ResponseEntity<?> result = controller.login(loginRequest, response);

            assertEquals(HttpStatus.UNAUTHORIZED, result.getStatusCode());
            assertEquals("Invalid login credentials", result.getBody());
        }
    }

    @Test
    void testRefreshAccess_Success() {
        TokenPair tokenPair = new TokenPair("new-access", "new-refresh");
        when(authApiService.refresh(eq("old-refresh"), any(HttpServletRequest.class))).thenReturn(tokenPair);
        when(jwtUtil.getTokenExpiration(anyString())).thenReturn(new Date(System.currentTimeMillis() + 3600000));

        try (var mockedCounter = mockStatic(Counter.class)) {
            Counter.Builder builder = mock(Counter.Builder.class, Answers.RETURNS_SELF);
            mockedCounter.when(() -> Counter.builder(anyString())).thenReturn(builder);
            when(builder.register(any(MeterRegistry.class))).thenReturn(counter);

            ResponseEntity<?> result = controller.refreshAccess("old-refresh", request, response);

            assertEquals(HttpStatus.OK, result.getStatusCode());
            AccessResponse body = (AccessResponse) result.getBody();
            assertNotNull(body);
            assertEquals(AccessResponseType.ACCESS_REFRESHED, body.getType());
            verify(response, times(2)).addHeader(eq("Set-Cookie"), anyString());
        }
    }

    @Test
    void testRefreshAccess_Failure() {
        when(authApiService.refresh(anyString(), any(HttpServletRequest.class))).thenThrow(new BadCredentialsException("Invalid"));

        try (var mockedCounter = mockStatic(Counter.class)) {
            Counter.Builder builder = mock(Counter.Builder.class, Answers.RETURNS_SELF);
            mockedCounter.when(() -> Counter.builder(anyString())).thenReturn(builder);
            when(builder.register(any(MeterRegistry.class))).thenReturn(counter);

            ResponseEntity<?> result = controller.refreshAccess("old-refresh", request, response);

            assertEquals(HttpStatus.UNAUTHORIZED, result.getStatusCode());
        }
    }

    @Test
    void testLogout_Success() {
        try (var mockedCounter = mockStatic(Counter.class)) {
            Counter.Builder builder = mock(Counter.Builder.class, Answers.RETURNS_SELF);
            mockedCounter.when(() -> Counter.builder(anyString())).thenReturn(builder);
            when(builder.register(any(MeterRegistry.class))).thenReturn(counter);

            ResponseEntity<?> result = controller.logout("refresh-token", response);

            assertEquals(HttpStatus.OK, result.getStatusCode());
            verify(authApiService).logout("refresh-token");
            verify(response, times(2)).addHeader(eq("Set-Cookie"), anyString());
        }
    }

    @Test
    void testLogout_NoToken() {
        try (var mockedCounter = mockStatic(Counter.class)) {
            Counter.Builder builder = mock(Counter.Builder.class, Answers.RETURNS_SELF);
            mockedCounter.when(() -> Counter.builder(anyString())).thenReturn(builder);
            when(builder.register(any(MeterRegistry.class))).thenReturn(counter);

            ResponseEntity<?> result = controller.logout(null, response);

            assertEquals(HttpStatus.UNAUTHORIZED, result.getStatusCode());
            verify(response, times(2)).addHeader(eq("Set-Cookie"), anyString());
        }
    }

    @Test
    void testLogoutAll_Success() {
        UserSecurity userSecurity = new UserSecurity();
        userSecurity.setId(1L);
        when(authentication.getPrincipal()).thenReturn(userSecurity);

        try (var mockedCounter = mockStatic(Counter.class)) {
            Counter.Builder builder = mock(Counter.Builder.class, Answers.RETURNS_SELF);
            mockedCounter.when(() -> Counter.builder(anyString())).thenReturn(builder);
            when(builder.register(any(MeterRegistry.class))).thenReturn(counter);

            ResponseEntity<?> result = controller.logoutAll(response, authentication);

            assertEquals(HttpStatus.OK, result.getStatusCode());
            verify(authApiService).logoutAll(1L);
            verify(response, times(2)).addHeader(eq("Set-Cookie"), anyString());
        }
    }
}
