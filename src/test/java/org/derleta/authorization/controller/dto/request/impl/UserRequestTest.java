package org.derleta.authorization.controller.dto.request.impl;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserRequestTest {

    @Test
    void testUserRequest() {
        String username = "testUser";
        String password = "password123";
        String email = "test@example.com";
        UserRequest request = new UserRequest(username, password, email);

        assertEquals(username, request.username());
        assertEquals(password, request.password());
        assertEquals(email, request.email());
    }
}
