package org.derleta.authorization.repository.mapper;

import org.derleta.authorization.domain.entity.UserEntity;
import org.junit.jupiter.api.Test;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserEntityMappingTest {

    @Test
    void testMapUserWithPrefix() throws SQLException {
        ResultSet rs = mock(ResultSet.class);
        String prefix = "u_";
        Timestamp now = new Timestamp(System.currentTimeMillis());

        when(rs.getLong(prefix + "user_id")).thenReturn(100L);
        when(rs.getString(prefix + "username")).thenReturn("testuser");
        when(rs.getString(prefix + "email")).thenReturn("test@example.com");
        when(rs.getString(prefix + "password")).thenReturn("password123");
        when(rs.getTimestamp(prefix + "created_at")).thenReturn(now);
        when(rs.getTimestamp(prefix + "updated_at")).thenReturn(now);
        when(rs.getBoolean(prefix + "verified")).thenReturn(true);
        when(rs.getBoolean(prefix + "blocked")).thenReturn(false);
        when(rs.getInt(prefix + "token_version")).thenReturn(1);

        UserEntity user = UserEntityMapping.mapUser(rs, prefix);

        assertEquals(100L, user.getUserId());
        assertEquals("testuser", user.getUsername());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("password123", user.getPassword());
        assertEquals(now, user.getCreatedAt());
        assertEquals(now, user.getUpdatedAt());
        assertTrue(user.getVerified());
        assertFalse(user.getBlocked());
        assertEquals(1, user.getTokenVersion());
    }

    @Test
    void testMapUserWithNullPrefix() throws SQLException {
        ResultSet rs = mock(ResultSet.class);
        Timestamp now = new Timestamp(System.currentTimeMillis());

        when(rs.getLong("user_id")).thenReturn(100L);
        when(rs.getString("username")).thenReturn("testuser");
        when(rs.getString("email")).thenReturn("test@example.com");
        when(rs.getString("password")).thenReturn("password123");
        when(rs.getTimestamp("created_at")).thenReturn(now);
        when(rs.getTimestamp("updated_at")).thenReturn(now);
        when(rs.getBoolean("verified")).thenReturn(true);
        when(rs.getBoolean("blocked")).thenReturn(false);
        when(rs.getInt("token_version")).thenReturn(1);

        UserEntity user = UserEntityMapping.mapUser(rs, null);

        assertEquals(100L, user.getUserId());
    }
}
