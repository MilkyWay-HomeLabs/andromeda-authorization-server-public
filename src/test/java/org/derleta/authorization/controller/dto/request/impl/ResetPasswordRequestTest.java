package org.derleta.authorization.controller.dto.request.impl;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ResetPasswordRequestTest {

    @Test
    void testResetPasswordRequest() {
        String email = "user@example.com";
        ResetPasswordRequest request = new ResetPasswordRequest(email);

        assertEquals(email, request.email());
    }
}
