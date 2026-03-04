package org.derleta.authorization.repository.mapper;

import org.derleta.authorization.domain.entity.UserRoleEntity;
import org.junit.jupiter.api.Test;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserRoleMapperTest {

    @Test
    void testMapRow() throws SQLException {
        ResultSet rs = mock(ResultSet.class);
        Timestamp now = new Timestamp(System.currentTimeMillis());

        when(rs.getLong("ur.user_role_id")).thenReturn(1L);
        
        // Mock UserEntityMapping values
        when(rs.getLong("u.user_id")).thenReturn(100L);
        when(rs.getString("u.username")).thenReturn("testuser");
        when(rs.getString("u.email")).thenReturn("test@example.com");
        when(rs.getString("u.password")).thenReturn("password123");
        when(rs.getTimestamp("u.created_at")).thenReturn(now);
        when(rs.getTimestamp("u.updated_at")).thenReturn(now);
        when(rs.getBoolean("u.verified")).thenReturn(true);
        when(rs.getBoolean("u.blocked")).thenReturn(false);
        when(rs.getInt("u.token_version")).thenReturn(1);

        // Mock RoleEntity values
        when(rs.getInt("r.role_id")).thenReturn(50);
        when(rs.getString("r.role_name")).thenReturn("USER");

        UserRoleMapper mapper = new UserRoleMapper();
        UserRoleEntity entity = mapper.mapRow(rs, 1);

        assertEquals(1L, entity.getUserRoleId());
        assertEquals(100L, entity.getUserEntity().getUserId());
        assertEquals("testuser", entity.getUserEntity().getUsername());
        assertEquals(50, entity.getRoleEntity().getRoleId());
        assertEquals("USER", entity.getRoleEntity().getRoleName());
    }
}
