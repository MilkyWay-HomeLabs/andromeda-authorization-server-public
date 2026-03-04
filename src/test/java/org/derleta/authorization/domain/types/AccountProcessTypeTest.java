package org.derleta.authorization.domain.types;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AccountProcessTypeTest {

    @Test
    void testAccountProcessTypeProperties() {
        assertEquals(1, AccountProcessType.USER_REGISTRATION.getId());
        assertEquals("User Registration", AccountProcessType.USER_REGISTRATION.getName());
        
        assertEquals(2, AccountProcessType.UNLOCK_ACCOUNT.getId());
        assertEquals("Unlock Account", AccountProcessType.UNLOCK_ACCOUNT.getName());
        
        assertEquals(3, AccountProcessType.CONFIRMATION_TOKEN.getId());
        assertEquals("Confirmation Token", AccountProcessType.CONFIRMATION_TOKEN.getName());
        
        assertEquals(4, AccountProcessType.RESET_PASSWORD.getId());
        assertEquals("Reset Password", AccountProcessType.RESET_PASSWORD.getName());
        
        assertEquals(5, AccountProcessType.CHANGE_PASSWORD.getId());
        assertEquals("Change Password", AccountProcessType.CHANGE_PASSWORD.getName());
    }

    @Test
    void testToString() {
        AccountProcessType type = AccountProcessType.USER_REGISTRATION;
        String expected = "AccountProcessType{id=1, name='User Registration'}";
        assertEquals(expected, type.toString());
    }
}
