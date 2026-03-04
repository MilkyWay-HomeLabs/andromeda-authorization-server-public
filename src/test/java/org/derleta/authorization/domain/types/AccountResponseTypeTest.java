package org.derleta.authorization.domain.types;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AccountResponseTypeTest {

    @Test
    void testAccountResponseTypeProperties() {
        AccountResponseType type = AccountResponseType.EMAIL_IS_NOT_UNIQUE;
        assertEquals(102, type.id);
        assertEquals(AppCode.ANDROMEDA_AUTH_SERVER, type.appCode);
        assertEquals(AccountProcessType.USER_REGISTRATION, type.processType);
        assertEquals("This email address is already in use.", type.info);
    }

    @Test
    void testNullType() {
        AccountResponseType type = AccountResponseType.NULL;
        assertEquals(0, type.id);
        assertNull(type.appCode);
        assertNull(type.processType);
        assertEquals("null", type.info);
    }

    @Test
    void testToString() {
        AccountResponseType type = AccountResponseType.ACCOUNT_CONFIRMED;
        String toString = type.toString();
        assertTrue(toString.contains("id=206"));
        assertTrue(toString.contains("appCode=" + AppCode.ANDROMEDA_AUTH_SERVER));
        assertTrue(toString.contains("processType=" + AccountProcessType.CONFIRMATION_TOKEN));
        assertTrue(toString.contains("info='Account confirmed.'"));
    }
}
