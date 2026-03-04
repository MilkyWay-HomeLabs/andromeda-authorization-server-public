package org.derleta.authorization.domain.types;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AppCodeTest {

    @Test
    void testAppCodeProperties() {
        AppCode appCode = AppCode.ANDROMEDA_AUTH_SERVER;
        assertEquals(1, appCode.id);
        assertEquals("Andromeda Authorization Server", appCode.name);
    }
}
