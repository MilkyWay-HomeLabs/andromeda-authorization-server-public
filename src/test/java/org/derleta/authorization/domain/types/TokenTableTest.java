package org.derleta.authorization.domain.types;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TokenTableTest {

    @Test
    void testTokenTableProperties() {
        assertEquals("refresh_tokens", TokenTable.REFRESH.getName());
        assertTrue(TokenTable.REFRESH.isHasRevoked());
        
        assertEquals("access_tokens", TokenTable.ACCESS.getName());
        assertTrue(TokenTable.ACCESS.isHasRevoked());
        
        assertEquals("confirmation_tokens", TokenTable.CONFIRMATION.getName());
        assertTrue(TokenTable.CONFIRMATION.isHasRevoked());
    }

    @Test
    void testToString() {
        TokenTable table = TokenTable.REFRESH;
        String expected = "TokenTable{name='refresh_tokens', hasRevoked=true}";
        assertEquals(expected, table.toString());
    }
}
