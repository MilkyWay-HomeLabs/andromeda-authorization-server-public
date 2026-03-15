package org.derleta.authorization.controller.mapper;

import org.derleta.authorization.domain.entity.token.AccessTokenEntity;
import org.derleta.authorization.domain.entity.token.ConfirmationTokenEntity;
import org.derleta.authorization.domain.entity.token.RefreshTokenEntity;
import org.derleta.authorization.domain.entity.token.TokenEntity;
import org.derleta.authorization.domain.model.AccessToken;
import org.derleta.authorization.domain.model.ConfirmationToken;
import org.derleta.authorization.domain.model.RefreshToken;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TokenApiMapperTest {

    private final Timestamp expiration = Timestamp.valueOf(LocalDateTime.now().plusHours(1));

    @Test
    void testToConfirmationToken() {
        ConfirmationTokenEntity entity = new ConfirmationTokenEntity(1L, 10L, "token123", expiration, 1, "jti123", false);
        
        ConfirmationToken token = TokenApiMapper.toConfirmationToken(entity);
        
        assertNotNull(token);
        assertEquals(1L, token.tokenId());
        assertEquals(10L, token.userId());
        assertEquals("token123", token.token());
        assertEquals(expiration, token.expirationDate());
        assertEquals(1, token.version());
        assertEquals("jti123", token.jti());
        assertFalse(token.revoked());
    }

    @Test
    void testToConfirmationToken_Null() {
        assertNull(TokenApiMapper.toConfirmationToken(null));
    }

    @Test
    void testToConfirmationTokens() {
        ConfirmationTokenEntity entity1 = new ConfirmationTokenEntity(1L, 10L, "t1", expiration, 1, "j1", false);
        ConfirmationTokenEntity entity2 = new ConfirmationTokenEntity(2L, 11L, "t2", expiration, 1, "j2", true);
        
        List<ConfirmationToken> tokens = TokenApiMapper.toConfirmationTokens(List.of(entity1, entity2));
        
        assertEquals(2, tokens.size());
        assertEquals(1L, tokens.get(0).tokenId());
        assertEquals(2L, tokens.get(1).tokenId());
    }

    @Test
    void testToAccessToken() {
        AccessTokenEntity entity = new AccessTokenEntity(1L, 10L, "atoken", expiration, 1, "ajti", false);
        
        AccessToken token = TokenApiMapper.toAccessToken(entity);
        
        assertNotNull(token);
        assertEquals(1L, token.tokenId());
        assertEquals("atoken", token.token());
    }

    @Test
    void testToAccessToken_Null() {
        assertNull(TokenApiMapper.toAccessToken(null));
    }

    @Test
    void testToAccessTokens() {
        AccessTokenEntity entity = new AccessTokenEntity(1L, 10L, "at", expiration, 1, "aj", false);
        List<AccessToken> tokens = TokenApiMapper.toAccessTokens(List.of(entity));
        assertEquals(1, tokens.size());
    }

    @Test
    void testToRefreshToken() {
        RefreshTokenEntity entity = new RefreshTokenEntity(1L, 10L, "rtoken", expiration, 1, "rjti", false);
        
        RefreshToken token = TokenApiMapper.toRefreshToken(entity);
        
        assertNotNull(token);
        assertEquals(1L, token.tokenId());
        assertEquals("rtoken", token.token());
    }

    @Test
    void testToRefreshToken_Null() {
        assertNull(TokenApiMapper.toRefreshToken(null));
    }

    @Test
    void testToRefreshTokens() {
        RefreshTokenEntity entity = new RefreshTokenEntity(1L, 10L, "rt", expiration, 1, "rj", false);
        List<RefreshToken> tokens = TokenApiMapper.toRefreshTokens(List.of(entity));
        assertEquals(1, tokens.size());
    }
}
