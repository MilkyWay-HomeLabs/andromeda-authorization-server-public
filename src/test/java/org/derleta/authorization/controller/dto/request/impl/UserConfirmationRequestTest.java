package org.derleta.authorization.controller.dto.request.impl;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserConfirmationRequestTest {

    @Test
    void testUserConfirmationRequest() {
        Long tokenId = 123L;
        String token = "veryLongConfirmationTokenValue";
        UserConfirmationRequest request = new UserConfirmationRequest(tokenId, token);

        assertEquals(tokenId, request.tokenId());
        assertEquals(token, request.token());
    }
}
