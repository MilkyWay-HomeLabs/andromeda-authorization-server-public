package org.derleta.authorization.domain.model;

import org.junit.jupiter.api.Test;
import java.sql.Timestamp;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {
    @Test
    void testUserRecord() {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        User user = new User(1L, "username", "password", "email@example.com", now, now, true, false, 0);

        assertEquals(1L, user.userId());
        assertEquals("username", user.username());
        assertEquals("password", user.password());
        assertEquals("email@example.com", user.email());
        assertEquals(now, user.createdAt());
        assertEquals(now, user.updatedAt());
        assertTrue(user.verified());
        assertFalse(user.blocked());
        assertEquals(0, user.tokenVersion());

        User sameUser = new User(1L, "username", "password", "email@example.com", now, now, true, false, 0);
        assertEquals(user, sameUser);
        assertEquals(user.hashCode(), sameUser.hashCode());
        assertEquals(user.toString(), sameUser.toString());

        User differentUser = new User(2L, "username2", "password", "email2@example.com", now, now, false, true, 1);
        assertNotEquals(user, differentUser);
    }
}
