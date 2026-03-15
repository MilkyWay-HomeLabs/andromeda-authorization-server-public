package org.derleta.authorization.controller.dto.request.impl;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TokenRequestTest {

    @Test
    void testTokenRequest() {
        String token = "someTokenValue";
        TokenRequest request = new TokenRequest();
        request.setToken(token);

        assertEquals(token, request.getToken());
    }
}
