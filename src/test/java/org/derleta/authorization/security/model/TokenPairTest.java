package org.derleta.authorization.security.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TokenPairTest {

    @Test
    void testTokenPair() {
        String access = "access";
        String refresh = "refresh";
        TokenPair pair = new TokenPair(access, refresh);

        assertEquals(access, pair.accessToken());
        assertEquals(refresh, pair.refreshToken());
    }

    @Test
    void testEqualsAndHashCode() {
        TokenPair pair1 = new TokenPair("a", "r");
        TokenPair pair2 = new TokenPair("a", "r");
        TokenPair pair3 = new TokenPair("a", "r2");

        assertEquals(pair1, pair2);
        assertNotEquals(pair1, pair3);
        assertEquals(pair1.hashCode(), pair2.hashCode());
    }
}
