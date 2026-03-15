package org.derleta.authorization.controller.dto.response;

import org.junit.jupiter.api.Test;
import java.sql.Timestamp;
import static org.junit.jupiter.api.Assertions.*;

class AccessTokenResponseTest {

    @Test
    void testGettersAndSetters() {
        AccessTokenResponse response = new AccessTokenResponse();
        long tokenId = 1L;
        long userId = 2L;
        Timestamp expirationDate = new Timestamp(System.currentTimeMillis());
        int version = 3;
        String jti = "jti-123";
        boolean revoked = true;

        response.setTokenId(tokenId);
        response.setUserId(userId);
        response.setExpirationDate(expirationDate);
        response.setVersion(version);
        response.setJti(jti);
        response.setRevoked(revoked);

        assertEquals(tokenId, response.getTokenId());
        assertEquals(userId, response.getUserId());
        assertEquals(expirationDate, response.getExpirationDate());
        assertEquals(version, response.getVersion());
        assertEquals(jti, response.getJti());
        assertTrue(response.isRevoked());
    }
}
