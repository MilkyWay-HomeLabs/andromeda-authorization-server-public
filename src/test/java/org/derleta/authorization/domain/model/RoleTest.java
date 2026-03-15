package org.derleta.authorization.domain.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RoleTest {
    @Test
    void testRoleRecord() {
        Role role = new Role(1, "ADMIN");

        assertEquals(1, role.roleId());
        assertEquals("ADMIN", role.roleName());

        Role sameRole = new Role(1, "ADMIN");
        assertEquals(role, sameRole);
        assertEquals(role.hashCode(), sameRole.hashCode());
        assertEquals(role.toString(), sameRole.toString());

        Role differentRole = new Role(2, "USER");
        assertNotEquals(role, differentRole);
    }
}
