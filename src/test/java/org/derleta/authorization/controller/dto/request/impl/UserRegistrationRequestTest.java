package org.derleta.authorization.controller.dto.request.impl;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserRegistrationRequestTest {

    @Test
    void testUserRegistrationRequest() {
        String username = "testUser";
        String password = "password123";
        String email = "test@example.com";
        UserRegistrationRequest request = new UserRegistrationRequest(username, password, email);

        assertEquals(username, request.username());
        assertEquals(password, request.password());
        assertEquals(email, request.email());
    }
}
