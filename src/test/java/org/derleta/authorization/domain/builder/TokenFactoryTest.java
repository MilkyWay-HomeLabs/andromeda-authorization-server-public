package org.derleta.authorization.domain.builder;

import org.derleta.authorization.domain.entity.token.AccessTokenEntity;
import org.derleta.authorization.domain.entity.token.ConfirmationTokenEntity;
import org.derleta.authorization.domain.entity.token.RefreshTokenEntity;
import org.derleta.authorization.domain.entity.token.TokenEntity;
import org.derleta.authorization.domain.types.TokenType;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

class TokenFactoryTest {

    @Test
    void testCreateAccessToken() {
        Timestamp expiration = new Timestamp(System.currentTimeMillis());
        TokenEntity token = TokenFactory.createToken(TokenType.ACCESS, 1L, 2L, "token", expiration, 3, "jti", false);

        assertInstanceOf(AccessTokenEntity.class, token);
        assertEquals(1L, token.getTokenId());
        assertEquals(2L, token.getUserId());
        assertEquals("token", token.getToken());
        assertEquals(expiration, token.getExpirationDate());
        assertEquals(3, token.getVersion());
        assertEquals("jti", token.getJti());
        assertFalse(token.isRevoked());
    }

    @Test
    void testCreateRefreshToken() {
        Timestamp expiration = new Timestamp(System.currentTimeMillis());
        TokenEntity token = TokenFactory.createToken(TokenType.REFRESH, 1L, 2L, "token", expiration, 3, "jti", false);

        assertInstanceOf(RefreshTokenEntity.class, token);
    }

    @Test
    void testCreateConfirmationToken() {
        Timestamp expiration = new Timestamp(System.currentTimeMillis());
        TokenEntity token = TokenFactory.createToken(TokenType.CONFIRMATION, 1L, 2L, "token", expiration, 3, "jti", false);

        assertInstanceOf(ConfirmationTokenEntity.class, token);
    }

    @Test
    void testCreateUnknownToken() {
        assertThrows(NullPointerException.class, () -> TokenFactory.createToken(null, 1L, 2L, "token", new Timestamp(0), 3, "jti", false));
    }
}
