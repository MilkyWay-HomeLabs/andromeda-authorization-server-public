package org.derleta.authorization.controller.dto.request.impl;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserUnlockRequestTest {

    @Test
    void testUserUnlockRequest() {
        Long userId = 456L;
        UserUnlockRequest request = new UserUnlockRequest(userId);

        assertEquals(userId, request.userId());
    }
}
