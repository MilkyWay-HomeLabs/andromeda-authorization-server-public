package org.derleta.authorization.controller.mapper;

import org.derleta.authorization.domain.entity.RoleEntity;
import org.derleta.authorization.domain.model.Role;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class RoleApiMapperTest {

    @Test
    void testToRole() {
        RoleEntity entity = new RoleEntity(1, "ADMIN");
        
        Role role = RoleApiMapper.toRole(entity);
        
        assertNotNull(role);
        assertEquals(1, role.roleId());
        assertEquals("ADMIN", role.roleName());
    }

    @Test
    void testToRole_Null() {
        assertNull(RoleApiMapper.toRole(null));
    }

    @Test
    void testToRoles() {
        RoleEntity entity1 = new RoleEntity(1, "ADMIN");
        RoleEntity entity2 = new RoleEntity(2, "USER");
        Set<RoleEntity> entities = Set.of(entity1, entity2);

        Set<Role> roles = RoleApiMapper.toRoles(entities);

        assertNotNull(roles);
        assertEquals(2, roles.size());
        assertTrue(roles.stream().anyMatch(r -> r.roleId() == 1 && r.roleName().equals("ADMIN")));
        assertTrue(roles.stream().anyMatch(r -> r.roleId() == 2 && r.roleName().equals("USER")));
    }
}
