package org.derleta.authorization.controller.mapper;

import org.derleta.authorization.domain.entity.RoleEntity;
import org.derleta.authorization.domain.entity.UserEntity;
import org.derleta.authorization.domain.entity.UserRoleEntity;
import org.derleta.authorization.domain.model.UserRoles;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserRolesApiMapperTest {

    @Test
    void testToUserRoles() {
        UserEntity userEntity = new UserEntity();
        userEntity.setUserId(1L);
        userEntity.setUsername("user1");
        
        RoleEntity role1 = new RoleEntity(1, "ADMIN");
        RoleEntity role2 = new RoleEntity(2, "USER");
        
        UserRoleEntity ure1 = new UserRoleEntity(101L, userEntity, role1);
        UserRoleEntity ure2 = new UserRoleEntity(102L, userEntity, role2);
        
        UserRoles userRoles = UserRolesApiMapper.toUserRoles(List.of(ure1, ure2));
        
        assertNotNull(userRoles);
        assertNotNull(userRoles.user());
        assertEquals(1L, userRoles.user().userId());
        assertEquals(2, userRoles.roles().size());
        assertTrue(userRoles.roles().stream().anyMatch(r -> r.roleId() == 1));
        assertTrue(userRoles.roles().stream().anyMatch(r -> r.roleId() == 2));
    }

    @Test
    void testToUserRoles_Empty() {
        UserRoles userRoles = UserRolesApiMapper.toUserRoles(List.of());
        assertNotNull(userRoles);
        assertNull(userRoles.user());
        assertTrue(userRoles.roles().isEmpty());
    }
}
