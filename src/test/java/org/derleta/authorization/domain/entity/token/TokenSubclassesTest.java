package org.derleta.authorization.domain.entity.token;

import org.junit.jupiter.api.Test;
import java.sql.Timestamp;
import static org.junit.jupiter.api.Assertions.*;

class TokenSubclassesTest {

    @Test
    void testAccessTokenEntity() {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        AccessTokenEntity token = new AccessTokenEntity(1L, 2L, "at", now, 3, "jti", false);
        assertEquals(1L, token.getTokenId());
        assertEquals("at", token.getToken());
        assertNotNull(new AccessTokenEntity());
    }

    @Test
    void testConfirmationTokenEntity() {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        ConfirmationTokenEntity token = new ConfirmationTokenEntity(1L, 2L, "ct", now, 3, "jti", false);
        assertEquals(1L, token.getTokenId());
        assertEquals("ct", token.getToken());
        assertNotNull(new ConfirmationTokenEntity());
    }

    @Test
    void testRefreshTokenEntity() {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        RefreshTokenEntity token = new RefreshTokenEntity(1L, 2L, "rt", now, 3, "jti", false);
        assertEquals(1L, token.getTokenId());
        assertEquals("rt", token.getToken());
        assertNotNull(new RefreshTokenEntity());
    }
}
