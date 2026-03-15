package org.derleta.authorization.domain.model;

import org.junit.jupiter.api.Test;
import java.sql.Timestamp;
import static org.junit.jupiter.api.Assertions.*;

class RefreshTokenTest {
    @Test
    void testRefreshTokenRecord() {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        RefreshToken token = new RefreshToken(1L, "token", 2L, now, 1, "jti", false);

        assertEquals(1L, token.tokenId());
        assertEquals("token", token.token());
        assertEquals(2L, token.userId());
        assertEquals(now, token.expirationDate());
        assertEquals(1, token.version());
        assertEquals("jti", token.jti());
        assertFalse(token.revoked());

        RefreshToken sameToken = new RefreshToken(1L, "token", 2L, now, 1, "jti", false);
        assertEquals(token, sameToken);
        assertEquals(token.hashCode(), sameToken.hashCode());
        assertEquals(token.toString(), sameToken.toString());

        RefreshToken differentToken = new RefreshToken(2L, "token2", 3L, now, 2, "jti2", true);
        assertNotEquals(token, differentToken);
    }
}
