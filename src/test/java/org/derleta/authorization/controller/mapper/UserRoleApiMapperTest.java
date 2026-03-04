package org.derleta.authorization.controller.mapper;

import org.derleta.authorization.domain.entity.RoleEntity;
import org.derleta.authorization.domain.entity.UserEntity;
import org.derleta.authorization.domain.entity.UserRoleEntity;
import org.derleta.authorization.domain.model.UserRole;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserRoleApiMapperTest {

    @Test
    void testToUserRoles() {
        UserEntity userEntity = new UserEntity();
        userEntity.setUserId(10L);
        RoleEntity roleEntity = new RoleEntity(1, "ADMIN");
        UserRoleEntity entity = new UserRoleEntity(100L, userEntity, roleEntity);
        
        UserRole model = UserRoleApiMapper.toUserRoles(entity);
        
        assertNotNull(model);
        assertEquals(100L, model.userRoleId());
        assertEquals(10L, model.user().userId());
        assertEquals(1, model.role().roleId());
    }

    @Test
    void testToUserRoles_Null() {
        assertNull(UserRoleApiMapper.toUserRoles(null));
    }

    @Test
    void testToUserRolesList() {
        UserRoleEntity entity = new UserRoleEntity();
        entity.setUserRoleId(100L);
        
        List<UserRole> list = UserRoleApiMapper.toUserRolesList(List.of(entity));
        
        assertEquals(1, list.size());
        assertEquals(100L, list.get(0).userRoleId());
    }

    @Test
    void testToUserRolesList_Null() {
        assertTrue(UserRoleApiMapper.toUserRolesList(null).isEmpty());
    }
}
