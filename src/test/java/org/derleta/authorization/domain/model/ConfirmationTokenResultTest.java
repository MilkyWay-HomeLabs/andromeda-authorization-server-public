package org.derleta.authorization.domain.model;

import org.junit.jupiter.api.Test;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.*;

class ConfirmationTokenResultTest {
    @Test
    void testConfirmationTokenResultRecord() {
        Date expiresAt = new Date();
        Date sessionExpiresAt = new Date(expiresAt.getTime() + 1000);
        ConfirmationTokenResult result = new ConfirmationTokenResult("token", "jti", expiresAt, sessionExpiresAt);

        assertEquals("token", result.token());
        assertEquals("jti", result.jti());
        assertEquals(expiresAt, result.expiresAt());
        assertEquals(sessionExpiresAt, result.sessionExpiresAt());

        ConfirmationTokenResult sameResult = new ConfirmationTokenResult("token", "jti", expiresAt, sessionExpiresAt);
        assertEquals(result, sameResult);
        assertEquals(result.hashCode(), sameResult.hashCode());
        assertEquals(result.toString(), sameResult.toString());

        ConfirmationTokenResult differentResult = new ConfirmationTokenResult("token2", "jti2", expiresAt, sessionExpiresAt);
        assertNotEquals(result, differentResult);
    }
}
