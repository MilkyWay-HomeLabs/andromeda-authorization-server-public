package org.derleta.authorization.controller.dto.request.impl;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ChangePasswordRequestTest {

    @Test
    void testChangePasswordRequest() {
        Long userId = 1L;
        String email = "user@example.com";
        String actualPassword = "oldPassword";
        String newPassword = "newPassword";
        ChangePasswordRequest request = new ChangePasswordRequest(userId, email, actualPassword, newPassword);

        assertEquals(userId, request.userId());
        assertEquals(email, request.email());
        assertEquals(actualPassword, request.actualPassword());
        assertEquals(newPassword, request.newPassword());
    }
}
