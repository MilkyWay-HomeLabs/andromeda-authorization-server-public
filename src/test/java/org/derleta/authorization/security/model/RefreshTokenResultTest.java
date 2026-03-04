package org.derleta.authorization.security.model;

import org.junit.jupiter.api.Test;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.*;

class RefreshTokenResultTest {

    @Test
    void testRefreshTokenResult() {
        String token = "test-token";
        String jti = "test-jti";
        Date expiresAt = new Date();
        Date sessionExpiresAt = new Date(expiresAt.getTime() + 1000);

        RefreshTokenResult result = new RefreshTokenResult(token, jti, expiresAt, sessionExpiresAt);

        assertEquals(token, result.token());
        assertEquals(jti, result.jti());
        assertEquals(expiresAt, result.expiresAt());
        assertEquals(sessionExpiresAt, result.sessionExpiresAt());
    }

    @Test
    void testEqualsAndHashCode() {
        Date now = new Date();
        RefreshTokenResult result1 = new RefreshTokenResult("t1", "j1", now, now);
        RefreshTokenResult result2 = new RefreshTokenResult("t1", "j1", now, now);
        RefreshTokenResult result3 = new RefreshTokenResult("t2", "j1", now, now);

        assertEquals(result1, result2);
        assertNotEquals(result1, result3);
        assertEquals(result1.hashCode(), result2.hashCode());
    }
}
