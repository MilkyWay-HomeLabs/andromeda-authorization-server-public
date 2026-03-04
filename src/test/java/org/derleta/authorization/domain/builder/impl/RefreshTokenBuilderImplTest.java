package org.derleta.authorization.domain.builder.impl;

import org.derleta.authorization.domain.model.RefreshToken;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

class RefreshTokenBuilderImplTest {

    @Test
    void testBuild() {
        long tokenId = 1L;
        long userId = 2L;
        String token = "refresh-token";
        Timestamp expirationDate = new Timestamp(System.currentTimeMillis());
        int version = 3;
        String jti = "jti-123";
        boolean revoked = true;

        RefreshTokenBuilderImpl builder = new RefreshTokenBuilderImpl();
        RefreshToken refreshToken = builder
                .tokenId(tokenId)
                .userId(userId)
                .token(token)
                .expirationDate(expirationDate)
                .version(version)
                .jti(jti)
                .revoked(revoked)
                .build();

        assertNotNull(refreshToken);
        assertEquals(tokenId, refreshToken.tokenId());
        assertEquals(userId, refreshToken.userId());
        assertEquals(token, refreshToken.token());
        assertEquals(expirationDate, refreshToken.expirationDate());
        assertEquals(version, refreshToken.version());
        assertEquals(jti, refreshToken.jti());
        assertTrue(refreshToken.revoked());
    }
}
