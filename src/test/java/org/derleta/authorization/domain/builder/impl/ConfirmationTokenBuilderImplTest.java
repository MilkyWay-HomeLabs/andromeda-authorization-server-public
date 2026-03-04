package org.derleta.authorization.domain.builder.impl;

import org.derleta.authorization.domain.model.ConfirmationToken;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

class ConfirmationTokenBuilderImplTest {

    @Test
    void testBuild() {
        long tokenId = 1L;
        long userId = 2L;
        String token = "confirmation-token";
        Timestamp expirationDate = new Timestamp(System.currentTimeMillis());
        int version = 3;
        String jti = "jti-123";
        boolean revoked = true;

        ConfirmationTokenBuilderImpl builder = new ConfirmationTokenBuilderImpl();
        ConfirmationToken confirmationToken = builder
                .tokenId(tokenId)
                .userId(userId)
                .token(token)
                .expirationDate(expirationDate)
                .version(version)
                .jti(jti)
                .revoked(revoked)
                .build();

        assertNotNull(confirmationToken);
        assertEquals(tokenId, confirmationToken.tokenId());
        assertEquals(userId, confirmationToken.userId());
        assertEquals(token, confirmationToken.token());
        assertEquals(expirationDate, confirmationToken.expirationDate());
        assertEquals(version, confirmationToken.version());
        assertEquals(jti, confirmationToken.jti());
        assertTrue(confirmationToken.revoked());
    }
}
