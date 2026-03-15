package org.derleta.authorization.domain.types;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AccessProcessTypeTest {

    @Test
    void testAccessProcessTypeProperties() {
        AccessProcessType type = AccessProcessType.REFRESH_ACCESS;
        assertEquals(6, type.getId());
        assertEquals("Refresh Access", type.getName());
    }

    @Test
    void testToString() {
        AccessProcessType type = AccessProcessType.REFRESH_ACCESS;
        String expected = "AccessProcessType{id=6, name='Refresh Access'}";
        assertEquals(expected, type.toString());
    }

    @Test
    void testValues() {
        AccessProcessType[] values = AccessProcessType.values();
        assertTrue(values.length > 0);
        assertEquals(AccessProcessType.REFRESH_ACCESS, AccessProcessType.valueOf("REFRESH_ACCESS"));
    }
}
