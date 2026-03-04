package org.derleta.authorization.controller.dto.response;

import org.junit.jupiter.api.Test;
import java.sql.Timestamp;
import static org.junit.jupiter.api.Assertions.*;

class RefreshTokenResponseTest {

    @Test
    void testGettersAndSetters() {
        RefreshTokenResponse response = new RefreshTokenResponse();
        long tokenId = 5L;
        long userId = 6L;
        String token = "refresh-token";
        Timestamp expirationDate = new Timestamp(System.currentTimeMillis());
        int version = 2;
        String jti = "jti-refresh";
        boolean revoked = true;

        response.setTokenId(tokenId);
        response.setUserId(userId);
        response.setToken(token);
        response.setExpirationDate(expirationDate);
        response.setVersion(version);
        response.setJti(jti);
        response.setRevoked(revoked);

        assertEquals(tokenId, response.getTokenId());
        assertEquals(userId, response.getUserId());
        assertEquals(token, response.getToken());
        assertEquals(expirationDate, response.getExpirationDate());
        assertEquals(version, response.getVersion());
        assertEquals(jti, response.getJti());
        assertTrue(response.isRevoked());
    }
}
