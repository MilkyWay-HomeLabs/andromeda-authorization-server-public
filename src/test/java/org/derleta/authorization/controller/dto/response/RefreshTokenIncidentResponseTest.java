package org.derleta.authorization.controller.dto.response;

import org.junit.jupiter.api.Test;
import java.sql.Timestamp;
import static org.junit.jupiter.api.Assertions.*;

class RefreshTokenIncidentResponseTest {

    @Test
    void testGettersAndSetters() {
        RefreshTokenIncidentResponse response = new RefreshTokenIncidentResponse();
        Long id = 1L;
        Long tokenId = 2L;
        Long userId = 3L;
        String jti = "jti-incident";
        int version = 1;
        Timestamp incidentTime = new Timestamp(System.currentTimeMillis());
        String ipAddress = "127.0.0.1";
        String userAgent = "Mozilla/5.0";
        String description = "Potential reuse";

        response.setId(id);
        response.setTokenId(tokenId);
        response.setUserId(userId);
        response.setJti(jti);
        response.setVersion(version);
        response.setIncidentTime(incidentTime);
        response.setIpAddress(ipAddress);
        response.setUserAgent(userAgent);
        response.setDescription(description);

        assertEquals(id, response.getId());
        assertEquals(tokenId, response.getTokenId());
        assertEquals(userId, response.getUserId());
        assertEquals(jti, response.getJti());
        assertEquals(version, response.getVersion());
        assertEquals(incidentTime, response.getIncidentTime());
        assertEquals(ipAddress, response.getIpAddress());
        assertEquals(userAgent, response.getUserAgent());
        assertEquals(description, response.getDescription());
    }
}
