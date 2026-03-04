package org.derleta.authorization.controller.dto.response;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserResponseTest {

    @Test
    void testGettersAndSetters() {
        UserResponse response = new UserResponse();
        long userId = 100L;
        String username = "testuser";
        String email = "test@example.com";

        response.setUserId(userId);
        response.setUsername(username);
        response.setEmail(email);

        assertEquals(userId, response.getUserId());
        assertEquals(username, response.getUsername());
        assertEquals(email, response.getEmail());
    }

    @Test
    void testConstructor() {
        long userId = 200L;
        String username = "admin";
        String email = "admin@example.com";
        UserResponse response = new UserResponse(userId, username, email);

        assertEquals(userId, response.getUserId());
        assertEquals(username, response.getUsername());
        assertEquals(email, response.getEmail());
    }
}
