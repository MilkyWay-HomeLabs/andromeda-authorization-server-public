package org.derleta.authorization.controller.dto.response;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AuthResponseTest {

    @Test
    void testAuthResponseRecord() {
        String username = "user123";
        String email = "user@example.com";

        AuthResponse response = new AuthResponse(username, email);

        assertEquals(username, response.username());
        assertEquals(email, response.email());
    }
}
