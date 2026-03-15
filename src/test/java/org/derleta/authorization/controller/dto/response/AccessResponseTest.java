package org.derleta.authorization.controller.dto.response;

import org.derleta.authorization.domain.types.AccessResponseType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AccessResponseTest {

    @Test
    void testAccessResponseConstructorAndGetters() {
        boolean success = true;
        AccessResponseType type = AccessResponseType.ACCESS_REFRESHED;

        AccessResponse response = new AccessResponse(success, type);

        assertTrue(response.isSuccess());
        assertEquals(type, response.getType());
    }

    @Test
    void testToString() {
        AccessResponse response = new AccessResponse(true, AccessResponseType.ACCESS_REFRESHED);
        String toString = response.toString();
        assertTrue(toString.contains("success=true"));
        assertTrue(toString.contains("type=" + AccessResponseType.ACCESS_REFRESHED));
    }

    @Test
    void testEqualsAndHashCode() {
        AccessResponse response1 = new AccessResponse(true, AccessResponseType.ACCESS_REFRESHED);
        AccessResponse response2 = new AccessResponse(true, AccessResponseType.ACCESS_REFRESHED);
        AccessResponse response3 = new AccessResponse(false, AccessResponseType.ACCESS_NOT_REFRESHED);

        assertEquals(response1, response2);
        assertNotEquals(response1, response3);
        assertEquals(response1.hashCode(), response2.hashCode());
        assertNotEquals(response1.hashCode(), response3.hashCode());
    }
}
