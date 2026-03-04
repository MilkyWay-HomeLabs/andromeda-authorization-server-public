package org.derleta.authorization.controller.dto.response;

import org.derleta.authorization.domain.types.AccountResponseType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AccountResponseTest {

    @Test
    void testConstructorAndGetters() {
        boolean success = true;
        AccountResponseType type = AccountResponseType.ACCOUNT_CONFIRMED;

        AccountResponse response = new AccountResponse(success, type);

        assertTrue(response.isSuccess());
        assertEquals(type, response.getType());
    }
}
