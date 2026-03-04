package org.derleta.authorization.repository.mapper;

import org.derleta.authorization.domain.entity.RoleEntity;
import org.junit.jupiter.api.Test;
import java.sql.ResultSet;
import java.sql.SQLException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RoleMapperTest {

    @Test
    void testMapRow() throws SQLException {
        ResultSet rs = mock(ResultSet.class);
        when(rs.getInt("role_id")).thenReturn(1);
        when(rs.getString("role_name")).thenReturn("ADMIN");

        RoleMapper mapper = new RoleMapper();
        RoleEntity entity = mapper.mapRow(rs, 1);

        assertEquals(1, entity.getRoleId());
        assertEquals("ADMIN", entity.getRoleName());
    }
}
