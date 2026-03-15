package org.derleta.authorization.domain.entity.token;

import org.junit.jupiter.api.Test;
import java.sql.Timestamp;
import static org.junit.jupiter.api.Assertions.*;

class RefreshTokenIncidentEntityTest {

    @Test
    void testRefreshTokenIncidentEntityProperties() {
        RefreshTokenIncidentEntity incident = new RefreshTokenIncidentEntity();
        Timestamp now = new Timestamp(System.currentTimeMillis());
        
        incident.setId(1L);
        incident.setTokenId(2L);
        incident.setUserId(3L);
        incident.setJti("some-jti");
        incident.setVersion(4);
        incident.setIncidentTime(now);
        incident.setIpAddress("127.0.0.1");
        incident.setUserAgent("Mozilla");
        incident.setDescription("Replay attack");

        assertEquals(1L, incident.getId());
        assertEquals(2L, incident.getTokenId());
        assertEquals(3L, incident.getUserId());
        assertEquals("some-jti", incident.getJti());
        assertEquals(4, incident.getVersion());
        assertEquals(now, incident.getIncidentTime());
        assertEquals("127.0.0.1", incident.getIpAddress());
        assertEquals("Mozilla", incident.getUserAgent());
        assertEquals("Replay attack", incident.getDescription());
    }

    @Test
    void testEqualsAndHashCode() {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        RefreshTokenIncidentEntity incident1 = new RefreshTokenIncidentEntity();
        incident1.setId(1L);
        incident1.setJti("jti");
        incident1.setIncidentTime(now);

        RefreshTokenIncidentEntity incident2 = new RefreshTokenIncidentEntity();
        incident2.setId(1L);
        incident2.setJti("jti");
        incident2.setIncidentTime(now);

        RefreshTokenIncidentEntity incident3 = new RefreshTokenIncidentEntity();
        incident3.setId(2L);

        assertEquals(incident1, incident2);
        assertNotEquals(incident1, incident3);
        assertEquals(incident1.hashCode(), incident2.hashCode());
        assertNotEquals(incident1.hashCode(), incident3.hashCode());
    }

    @Test
    void testToString() {
        RefreshTokenIncidentEntity incident = new RefreshTokenIncidentEntity();
        incident.setId(1L);
        incident.setDescription("Test incident");
        String toString = incident.toString();
        assertTrue(toString.contains("id=1"));
        assertTrue(toString.contains("description='Test incident'"));
    }
}
