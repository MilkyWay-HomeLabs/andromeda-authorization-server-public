package org.derleta.authorization.controller;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.derleta.authorization.controller.dto.request.impl.AuthLoginRequest;
import org.derleta.authorization.controller.dto.response.AccessResponse;
import org.derleta.authorization.controller.dto.response.AuthResponse;
import org.derleta.authorization.domain.types.AccessResponseType;
import org.derleta.authorization.security.api.AuthApiService;
import org.derleta.authorization.security.model.TokenPair;
import org.derleta.authorization.security.model.UserSecurity;
import org.derleta.authorization.security.util.JwtTokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private static final String COOKIE_ACCESS_NAME = "accessToken";
    private static final String COOKIE_REFRESH_NAME = "refreshToken";

    private final AuthenticationManager authManager;
    private final JwtTokenUtil jwtUtil;
    private final AuthApiService authApiService;
    private final MeterRegistry meterRegistry;

    @Autowired
    public AuthController(
            AuthenticationManager authManager,
            JwtTokenUtil jwtUtil,
            AuthApiService authApiService,
            MeterRegistry meterRegistry
    ) {
        this.authManager = authManager;
        this.jwtUtil = jwtUtil;
        this.authApiService = authApiService;
        this.meterRegistry = meterRegistry;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid AuthLoginRequest request, HttpServletResponse response) {
        try {
            Authentication authentication = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getLogin(), request.getPassword())
            );
            ResponseEntity<?> entity = buildSuccessLoginResponse(authentication, response);
            countHttp("login", String.valueOf(entity.getStatusCode().value()));
            return entity;
        } catch (BadCredentialsException ex) {
            log.warn("Failed login for identifier={}", request.getLogin());
            countHttp("login", "401");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid login credentials");
        }
    }

    @PostMapping("/refresh-access")
    public ResponseEntity<?> refreshAccess(
            @CookieValue(value = COOKIE_REFRESH_NAME, required = false) String refreshToken,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        try {
            if (refreshToken == null || refreshToken.isBlank()) {
                throw new BadCredentialsException("Missing refresh token");
            }

            TokenPair tokenPair = authApiService.refresh(refreshToken, request);
            addCookiesToResponse(response, tokenPair.accessToken(), tokenPair.refreshToken());

            AccessResponse body = new AccessResponse(true, AccessResponseType.ACCESS_REFRESHED);
            countHttp("refresh_access", "200");
            return ResponseEntity.ok(body);
        } catch (BadCredentialsException ex) {
            AccessResponse body = new AccessResponse(false, AccessResponseType.ACCESS_NOT_REFRESHED);
            countHttp("refresh_access", "401");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
        } catch (RuntimeException ex) {
            AccessResponse body = new AccessResponse(false, AccessResponseType.ACCESS_NOT_REFRESHED);
            countHttp("refresh_access", "500");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            @CookieValue(value = COOKIE_REFRESH_NAME, required = false) String refreshToken,
            HttpServletResponse response
    ) {
        if (refreshToken == null || refreshToken.isBlank()) {
            clearAuthCookies(response);
            countHttp("logout", "401");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            authApiService.logout(refreshToken);
            clearAuthCookies(response);
            countHttp("logout", "200");
            return ResponseEntity.ok().build();
        } catch (BadCredentialsException ex) {
            clearAuthCookies(response);
            countHttp("logout", "401");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (RuntimeException ex) {
            clearAuthCookies(response);
            countHttp("logout", "500");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/logout-all")
    public ResponseEntity<?> logoutAll(HttpServletResponse response, Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof UserSecurity user)) {
            countHttp("logout_all", "401");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            authApiService.logoutAll(user.getId());
            clearAuthCookies(response);
            countHttp("logout_all", "200");
            return ResponseEntity.ok().build();
        } catch (BadCredentialsException ex) {
            countHttp("logout_all", "401");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (RuntimeException ex) {
            countHttp("logout_all", "500");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private void countHttp(final String endpoint, final String status) {
        Counter.builder("auth_http_requests_total")
                .tag("endpoint", endpoint)
                .tag("status", status)
                .register(meterRegistry)
                .increment();
    }

    private void clearAuthCookies(HttpServletResponse response) {
        ResponseCookie clearAccess = ResponseCookie.from(COOKIE_ACCESS_NAME, "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();

        ResponseCookie clearRefresh = ResponseCookie.from(COOKIE_REFRESH_NAME, "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, clearAccess.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, clearRefresh.toString());
    }

    @NotNull
    private ResponseEntity<?> buildSuccessLoginResponse(Authentication authentication, HttpServletResponse httpServletResponse) {
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof UserSecurity user)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid login credentials");
        }

        TokenPair tokenPair = authApiService.issueTokenPairForLogin(user);
        addCookiesToResponse(httpServletResponse, tokenPair.accessToken(), tokenPair.refreshToken());

        AuthResponse response = new AuthResponse(user.getUsername(), user.getEmail());
        return ResponseEntity.ok(response);
    }

    private void addCookiesToResponse(HttpServletResponse response, final String accessToken, final String refreshToken) {
        ResponseCookie accessCookie = buildAccessTokenCookie(accessToken);
        ResponseCookie refreshCookie = buildRefreshTokenCookie(refreshToken);

        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
    }

    private ResponseCookie buildAccessTokenCookie(String token) {
        long maxAgeSeconds = getMaxAgeSecondsForAccessToken(token);
        return ResponseCookie.from(COOKIE_ACCESS_NAME, token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(maxAgeSeconds)
                .sameSite("Lax")
                .build();
    }

    private ResponseCookie buildRefreshTokenCookie(String token) {
        long maxAgeSeconds = getMaxAgeSecondsForRefreshToken(token);
        return ResponseCookie.from(COOKIE_REFRESH_NAME, token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(maxAgeSeconds)
                .sameSite("Lax")
                .build();
    }

    private long getMaxAgeSecondsForAccessToken(String token) {
        long secondsUntilTokenExp = getSecondsUntilTokenExpiration(token);
        return Math.min(secondsUntilTokenExp, jwtUtil.JWT_ACCESS_EXPIRATION);
    }

    private long getMaxAgeSecondsForRefreshToken(String token) {
        long secondsUntilTokenExp = getSecondsUntilTokenExpiration(token);
        return Math.min(secondsUntilTokenExp, jwtUtil.JWT_REFRESH_EXPIRATION);
    }

    private long getSecondsUntilTokenExpiration(String token) {
        java.util.Date exp = jwtUtil.getTokenExpiration(token);
        if (exp == null) return 0L;

        long nowMillis = System.currentTimeMillis();
        long diffMillis = exp.getTime() - nowMillis;
        if (diffMillis <= 0) return 0L;

        return Duration.ofMillis(diffMillis).getSeconds();
    }
}
