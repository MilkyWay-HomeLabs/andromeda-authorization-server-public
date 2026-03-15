package org.derleta.authorization.controller.dto.response;

import org.junit.jupiter.api.Test;
import java.sql.Timestamp;
import static org.junit.jupiter.api.Assertions.*;

class ConfirmationTokenResponseTest {

    @Test
    void testGettersAndSetters() {
        ConfirmationTokenResponse response = new ConfirmationTokenResponse();
        long tokenId = 10L;
        long userId = 20L;
        String token = "conf-token";
        Timestamp expirationDate = new Timestamp(System.currentTimeMillis());
        int version = 1;
        String jti = "jti-conf";
        boolean revoked = false;

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
        assertFalse(response.isRevoked());
    }
}
