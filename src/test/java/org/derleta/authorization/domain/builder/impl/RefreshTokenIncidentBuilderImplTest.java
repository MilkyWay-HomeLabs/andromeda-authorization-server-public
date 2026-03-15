package org.derleta.authorization.domain.builder.impl;

import org.derleta.authorization.domain.entity.token.RefreshTokenIncidentEntity;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

class RefreshTokenIncidentBuilderImplTest {

    @Test
    void testBuild() {
        Long id = 1L;
        Long tokenId = 2L;
        Long userId = 3L;
        String jti = "jti-123";
        int version = 4;
        Timestamp incidentTime = new Timestamp(System.currentTimeMillis());
        String ipAddress = "127.0.0.1";
        String userAgent = "Mozilla/5.0";
        String description = "Security incident";

        RefreshTokenIncidentBuilderImpl builder = new RefreshTokenIncidentBuilderImpl();
        RefreshTokenIncidentEntity incident = builder
                .id(id)
                .tokenId(tokenId)
                .userId(userId)
                .jti(jti)
                .version(version)
                .incidentTime(incidentTime)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .description(description)
                .build();

        assertNotNull(incident);
        assertEquals(id, incident.getId());
        assertEquals(tokenId, incident.getTokenId());
        assertEquals(userId, incident.getUserId());
        assertEquals(jti, incident.getJti());
        assertEquals(version, incident.getVersion());
        assertEquals(incidentTime, incident.getIncidentTime());
        assertEquals(ipAddress, incident.getIpAddress());
        assertEquals(userAgent, incident.getUserAgent());
        assertEquals(description, incident.getDescription());
    }
}
