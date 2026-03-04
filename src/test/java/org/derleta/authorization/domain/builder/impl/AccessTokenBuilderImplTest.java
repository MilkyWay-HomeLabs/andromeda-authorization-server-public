package org.derleta.authorization.domain.builder.impl;

import org.derleta.authorization.domain.model.AccessToken;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

class AccessTokenBuilderImplTest {

    @Test
    void testBuild() {
        long tokenId = 1L;
        long userId = 2L;
        String token = "access-token";
        Timestamp expirationDate = new Timestamp(System.currentTimeMillis());
        int version = 3;
        String jti = "jti-123";
        boolean revoked = true;

        AccessTokenBuilderImpl builder = new AccessTokenBuilderImpl();
        AccessToken accessToken = builder
                .tokenId(tokenId)
                .userId(userId)
                .token(token)
                .expirationDate(expirationDate)
                .version(version)
                .jti(jti)
                .revoked(revoked)
                .build();

        assertNotNull(accessToken);
        assertEquals(tokenId, accessToken.tokenId());
        assertEquals(userId, accessToken.userId());
        assertEquals(token, accessToken.token());
        assertEquals(expirationDate, accessToken.expirationDate());
        assertEquals(version, accessToken.version());
        assertEquals(jti, accessToken.jti());
        assertTrue(accessToken.revoked());
    }
}
