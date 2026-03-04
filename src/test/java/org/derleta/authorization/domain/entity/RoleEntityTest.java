package org.derleta.authorization.domain.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RoleEntityTest {

    @Test
    void testRoleEntityProperties() {
        RoleEntity role = new RoleEntity();
        role.setRoleId(1);
        role.setRoleName("ROLE_USER");

        assertEquals(1, role.getRoleId());
        assertEquals("ROLE_USER", role.getRoleName());
    }

    @Test
    void testConstructor() {
        RoleEntity role = new RoleEntity(2, "ROLE_ADMIN");
        assertEquals(2, role.getRoleId());
        assertEquals("ROLE_ADMIN", role.getRoleName());
    }

    @Test
    void testEqualsAndHashCode() {
        RoleEntity role1 = new RoleEntity(1, "ROLE_USER");
        RoleEntity role2 = new RoleEntity(1, "ROLE_USER");
        RoleEntity role3 = new RoleEntity(2, "ROLE_ADMIN");

        assertEquals(role1, role2);
        assertNotEquals(role1, role3);
        assertEquals(role1.hashCode(), role2.hashCode());
        assertNotEquals(role1.hashCode(), role3.hashCode());
    }

    @Test
    void testToString() {
        RoleEntity role = new RoleEntity(1, "ROLE_USER");
        String toString = role.toString();
        assertTrue(toString.contains("roleId=1"));
        assertTrue(toString.contains("roleName='ROLE_USER'"));
    }
}
