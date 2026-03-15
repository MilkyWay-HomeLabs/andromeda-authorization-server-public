package org.derleta.authorization.domain.entity.token;

import org.junit.jupiter.api.Test;
import java.sql.Timestamp;
import static org.junit.jupiter.api.Assertions.*;

class TokenEntityTest {

    // Concrete implementation for testing the abstract class
    private static class TestTokenEntity extends TokenEntity {
        public TestTokenEntity() {}
        public TestTokenEntity(long tokenId, long userId, String token, Timestamp expirationDate, int version, String jti, boolean revoked) {
            super(tokenId, userId, token, expirationDate, version, jti, revoked);
        }
    }

    @Test
    void testTokenEntityProperties() {
        TestTokenEntity token = new TestTokenEntity();
        Timestamp now = new Timestamp(System.currentTimeMillis());
        
        token.setTokenId(1L);
        token.setUserId(2L);
        token.setToken("some-token");
        token.setExpirationDate(now);
        token.setVersion(3);
        token.setJti("some-jti");
        token.setRevoked(true);

        assertEquals(1L, token.getTokenId());
        assertEquals(2L, token.getUserId());
        assertEquals("some-token", token.getToken());
        assertEquals(now, token.getExpirationDate());
        assertEquals(3, token.getVersion());
        assertEquals("some-jti", token.getJti());
        assertTrue(token.isRevoked());
    }

    @Test
    void testConstructor() {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        TestTokenEntity token = new TestTokenEntity(1L, 2L, "some-token", now, 3, "some-jti", true);
        
        assertEquals(1L, token.getTokenId());
        assertEquals(2L, token.getUserId());
        assertEquals("some-token", token.getToken());
        assertEquals(now, token.getExpirationDate());
        assertEquals(3, token.getVersion());
        assertEquals("some-jti", token.getJti());
        assertTrue(token.isRevoked());
    }

    @Test
    void testToString() {
        TestTokenEntity token = new TestTokenEntity();
        token.setTokenId(1L);
        String toString = token.toString();
        assertTrue(toString.contains("tokenId=1"));
        assertTrue(toString.contains("TestTokenEntity"));
    }
}
