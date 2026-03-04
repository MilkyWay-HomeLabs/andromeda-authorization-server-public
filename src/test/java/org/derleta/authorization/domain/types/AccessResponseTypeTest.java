package org.derleta.authorization.domain.types;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AccessResponseTypeTest {

    @Test
    void testAccessResponseTypeProperties() {
        AccessResponseType type = AccessResponseType.ACCESS_REFRESHED;
        assertEquals(601, type.id);
        assertEquals(AppCode.ANDROMEDA_AUTH_SERVER, type.appCode);
        assertEquals(AccessProcessType.REFRESH_ACCESS, type.processType);
        assertEquals("Access was refreshed, new access token was generated and sent to client.", type.info);
    }

    @Test
    void testNullType() {
        AccessResponseType type = AccessResponseType.NULL;
        assertEquals(0, type.id);
        assertNull(type.appCode);
        assertNull(type.processType);
        assertEquals("null", type.info);
    }

    @Test
    void testToString() {
        AccessResponseType type = AccessResponseType.ACCESS_REFRESHED;
        String toString = type.toString();
        assertTrue(toString.contains("id=601"));
        assertTrue(toString.contains("appCode=" + AppCode.ANDROMEDA_AUTH_SERVER));
        assertTrue(toString.contains("processType=" + AccessProcessType.REFRESH_ACCESS));
        assertTrue(toString.contains("info='Access was refreshed, new access token was generated and sent to client.'"));
    }
}
