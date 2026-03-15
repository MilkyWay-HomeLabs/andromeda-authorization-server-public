package org.derleta.authorization.controller.dto.request.impl;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AuthLoginRequestTest {

    @Test
    void testAuthLoginRequest() {
        String login = "testUser";
        String password = "testPassword";
        AuthLoginRequest request = new AuthLoginRequest(login, password);

        assertEquals(login, request.getLogin());
        assertEquals(password, request.getPassword());
    }

    @Test
    void testAuthLoginRequestNoArgConstructor() {
        AuthLoginRequest request = new AuthLoginRequest();
        assertNull(request.getLogin());
        assertNull(request.getPassword());
    }
}
