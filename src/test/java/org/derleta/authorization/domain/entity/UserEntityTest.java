package org.derleta.authorization.domain.entity;

import org.junit.jupiter.api.Test;
import java.sql.Timestamp;
import static org.junit.jupiter.api.Assertions.*;

class UserEntityTest {

    @Test
    void testUserEntityProperties() {
        UserEntity user = new UserEntity();
        Timestamp now = new Timestamp(System.currentTimeMillis());
        
        user.setUserId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        user.setVerified(true);
        user.setBlocked(false);
        user.setTokenVersion(1);

        assertEquals(1L, user.getUserId());
        assertEquals("testuser", user.getUsername());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("password", user.getPassword());
        assertEquals(now, user.getCreatedAt());
        assertEquals(now, user.getUpdatedAt());
        assertTrue(user.getVerified());
        assertFalse(user.getBlocked());
        assertEquals(1, user.getTokenVersion());
    }

    @Test
    void testConstructor() {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        UserEntity user = new UserEntity(1L, "testuser", "test@example.com", "password", now, now, true, false, 1);
        
        assertEquals(1L, user.getUserId());
        assertEquals("testuser", user.getUsername());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("password", user.getPassword());
        assertEquals(now, user.getCreatedAt());
        assertEquals(now, user.getUpdatedAt());
        assertTrue(user.getVerified());
        assertFalse(user.getBlocked());
        assertEquals(1, user.getTokenVersion());
    }

    @Test
    void testEqualsAndHashCode() {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        UserEntity user1 = new UserEntity(1L, "testuser", "test@example.com", "password", now, now, true, false, 1);
        UserEntity user2 = new UserEntity(1L, "testuser", "test@example.com", "password", now, now, true, false, 1);
        UserEntity user3 = new UserEntity(2L, "other", "other@example.com", "pass", now, now, false, true, 2);

        assertEquals(user1, user2);
        assertNotEquals(user1, user3);
        assertEquals(user1.hashCode(), user2.hashCode());
        assertNotEquals(user1.hashCode(), user3.hashCode());
    }

    @Test
    void testToString() {
        UserEntity user = new UserEntity();
        user.setUserId(1L);
        user.setUsername("testuser");
        String toString = user.toString();
        assertTrue(toString.contains("userId=1"));
        assertTrue(toString.contains("username='testuser'"));
    }
}
