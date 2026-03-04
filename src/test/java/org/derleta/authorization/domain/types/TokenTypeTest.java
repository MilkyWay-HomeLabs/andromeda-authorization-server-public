package org.derleta.authorization.domain.types;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TokenTypeTest {

    @Test
    void testTokenTypeProperties() {
        assertEquals(1, TokenType.CONFIRMATION.getId());
        assertEquals("Confirmation Token", TokenType.CONFIRMATION.getName());
        
        assertEquals(2, TokenType.ACCESS.getId());
        assertEquals("Access Token", TokenType.ACCESS.getName());
        
        assertEquals(3, TokenType.REFRESH.getId());
        assertEquals("Refresh Token", TokenType.REFRESH.getName());
    }
}
