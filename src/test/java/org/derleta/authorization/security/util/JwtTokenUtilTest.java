package org.derleta.authorization.security.util;

import io.jsonwebtoken.Claims;
import org.derleta.authorization.domain.model.ConfirmationTokenResult;
import org.derleta.authorization.security.model.RefreshTokenResult;
import org.derleta.authorization.security.model.RoleSecurity;
import org.derleta.authorization.security.model.UserSecurity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenUtilTest {

    private JwtTokenUtil jwtTokenUtil;
    // Base64 encoded string of at least 32 bytes (256 bits)
    private final String SECRET = "YXNkZmdoamtsIUAjJCVeJiooKSlhc2RmZ2hqa2whQCMkJV4mKigpKQ==";
    private final int ACCESS_EXP = 3600000; // 1 hour
    private final long REFRESH_EXP = 86400000; // 24 hours
    private final long CONFIRM_EXP = 1800000; // 30 mins
    private final long SESSION_EXP = 604800000; // 7 days

    private UserSecurity testUser;

    @BeforeEach
    void setUp() {
        jwtTokenUtil = new JwtTokenUtil();
        ReflectionTestUtils.setField(jwtTokenUtil, "SECRET_KEY", SECRET);
        ReflectionTestUtils.setField(jwtTokenUtil, "JWT_ACCESS_EXPIRATION", ACCESS_EXP);
        ReflectionTestUtils.setField(jwtTokenUtil, "JWT_REFRESH_EXPIRATION", REFRESH_EXP);
        ReflectionTestUtils.setField(jwtTokenUtil, "JWT_CONFIRMATION_EXPIRATION", CONFIRM_EXP);
        ReflectionTestUtils.setField(jwtTokenUtil, "JWT_SESSION_EXPIRATION", SESSION_EXP);

        testUser = new UserSecurity();
        testUser.setId(123L);
        testUser.setEmail("test@example.com");
        testUser.setTokenVersion(1);
        testUser.setRoles(Set.of(new RoleSecurity(1, "ROLE_USER")));
    }

    @Test
    void testValidateConfigSuccess() {
        assertDoesNotThrow(() -> jwtTokenUtil.validateConfig());
    }

    @Test
    void testValidateConfigThrowsException() {
        ReflectionTestUtils.setField(jwtTokenUtil, "SECRET_KEY", "");
        assertThrows(IllegalStateException.class, () -> jwtTokenUtil.validateConfig());

        ReflectionTestUtils.setField(jwtTokenUtil, "SECRET_KEY", "invalid-base64-!!!");
        assertThrows(IllegalStateException.class, () -> jwtTokenUtil.validateConfig());
        
        // Too short (less than 32 bytes)
        // "YWJjZGVmZ2hpamtsbW5vcHFyc3R1dnd4eXo=" decodes to 26 bytes
        ReflectionTestUtils.setField(jwtTokenUtil, "SECRET_KEY", "YWJjZGVmZ2hpamtsbW5vcHFyc3R1dnd4eXo="); 
        assertThrows(IllegalStateException.class, () -> jwtTokenUtil.validateConfig());
    }

    @Test
    void testValidateConfigInvalidExpirations() {
        ReflectionTestUtils.setField(jwtTokenUtil, "JWT_ACCESS_EXPIRATION", 0);
        assertThrows(IllegalStateException.class, () -> jwtTokenUtil.validateConfig());
        
        ReflectionTestUtils.setField(jwtTokenUtil, "JWT_ACCESS_EXPIRATION", 3600);
        ReflectionTestUtils.setField(jwtTokenUtil, "JWT_REFRESH_EXPIRATION", 0L);
        assertThrows(IllegalStateException.class, () -> jwtTokenUtil.validateConfig());
    }

    @Test
    void testGenerateAndValidateAccessToken() {
        String token = jwtTokenUtil.generateAccessToken(testUser);
        assertNotNull(token);
        assertTrue(jwtTokenUtil.validateJWTToken(token));
        
        assertEquals(123L, jwtTokenUtil.getUserId(token));
        assertEquals(1, jwtTokenUtil.getTokenVersion(token));
        assertNotNull(jwtTokenUtil.getJti(token));
        assertNotNull(jwtTokenUtil.getTokenExpiration(token));
        assertFalse(jwtTokenUtil.isRevokedClaim(token));
    }

    @Test
    void testGenerateAndValidateRefreshToken() {
        RefreshTokenResult result = jwtTokenUtil.generateRefreshToken(testUser);
        assertNotNull(result);
        assertNotNull(result.token());
        assertTrue(jwtTokenUtil.validateJWTToken(result.token()));
        
        assertEquals(123L, jwtTokenUtil.getUserId(result.token()));
        assertEquals(1, jwtTokenUtil.getTokenVersion(result.token()));
        assertNotNull(jwtTokenUtil.getSessionExpiration(result.token()));
    }

    @Test
    void testGenerateAndValidateConfirmationToken() {
        ConfirmationTokenResult result = jwtTokenUtil.generateConfirmationToken(123L, 2);
        assertNotNull(result);
        assertNotNull(result.token());
        assertTrue(jwtTokenUtil.validateJWTToken(result.token()));
        
        assertEquals(123L, jwtTokenUtil.getUserId(result.token()));
        assertEquals(2, jwtTokenUtil.getTokenVersion(result.token()));
    }

    @Test
    void testParseClaims() {
        String token = jwtTokenUtil.generateAccessToken(testUser);
        Claims claims = jwtTokenUtil.parseClaims(token);
        assertNotNull(claims);
        assertEquals("123", claims.getSubject());
        assertEquals(1, (Integer) claims.get("version"));
    }

    @Test
    void testInvalidToken() {
        assertFalse(jwtTokenUtil.validateJWTToken("invalid.token.here"));
        assertFalse(jwtTokenUtil.validateJWTToken(null));
        assertFalse(jwtTokenUtil.validateJWTToken(""));
    }

    @Test
    void testExpiredToken() {
        // Set very short expiration
        ReflectionTestUtils.setField(jwtTokenUtil, "JWT_ACCESS_EXPIRATION", -1000); // 1 second ago
        // Wait, validateConfig throws exception if <= 0.
        // But generateToken uses what's in the field.
        
        String token = jwtTokenUtil.generateAccessToken(testUser);
        // It might already be expired because we subtracted 1 second from "now"
        assertFalse(jwtTokenUtil.validateJWTToken(token));
    }

    @Test
    void testGetTokenVersionEdgeCases() {
        String token = jwtTokenUtil.generateAccessToken(testUser);
        assertEquals(1, jwtTokenUtil.getTokenVersion(token));
        
        // Mock token version as string
        // Actually JwtTokenUtil uses Jwts.builder().claim(CLAIM_VERSION, user.getTokenVersion())
        // which will be Integer.
    }
}
