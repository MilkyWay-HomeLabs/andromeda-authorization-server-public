package org.derleta.authorization.domain.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserRoleEntityTest {

    @Test
    void testUserRoleEntityProperties() {
        UserRoleEntity userRole = new UserRoleEntity();
        UserEntity user = new UserEntity();
        RoleEntity role = new RoleEntity();
        
        userRole.setUserRoleId(1L);
        userRole.setUserEntity(user);
        userRole.setRoleEntity(role);

        assertEquals(1L, userRole.getUserRoleId());
        assertEquals(user, userRole.getUserEntity());
        assertEquals(role, userRole.getRoleEntity());
    }

    @Test
    void testConstructor() {
        UserEntity user = new UserEntity();
        RoleEntity role = new RoleEntity();
        UserRoleEntity userRole = new UserRoleEntity(1L, user, role);
        
        assertEquals(1L, userRole.getUserRoleId());
        assertEquals(user, userRole.getUserEntity());
        assertEquals(role, userRole.getRoleEntity());
    }

    @Test
    void testEqualsAndHashCode() {
        UserEntity user = new UserEntity();
        RoleEntity role = new RoleEntity();
        UserRoleEntity ur1 = new UserRoleEntity(1L, user, role);
        UserRoleEntity ur2 = new UserRoleEntity(1L, user, role);
        UserRoleEntity ur3 = new UserRoleEntity(2L, user, role);

        assertEquals(ur1, ur2);
        assertNotEquals(ur1, ur3);
        assertEquals(ur1.hashCode(), ur2.hashCode());
        assertNotEquals(ur1.hashCode(), ur3.hashCode());
    }

    @Test
    void testToString() {
        UserRoleEntity userRole = new UserRoleEntity();
        userRole.setUserRoleId(1L);
        String toString = userRole.toString();
        // The implementation has a typo "UserRolesEntity" but we test what is there
        assertTrue(toString.contains("userRoleId=1"));
    }
}
