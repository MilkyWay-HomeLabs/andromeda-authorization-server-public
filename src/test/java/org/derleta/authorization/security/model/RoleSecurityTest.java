package org.derleta.authorization.security.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RoleSecurityTest {

    @Test
    void testConstructorsAndGetters() {
        RoleSecurity role1 = new RoleSecurity("ROLE_USER");
        assertEquals(0, role1.getId());
        assertEquals("ROLE_USER", role1.getName());

        RoleSecurity role2 = new RoleSecurity(1, "ROLE_ADMIN");
        assertEquals(1, role2.getId());
        assertEquals("ROLE_ADMIN", role2.getName());
    }

    @Test
    void testToString() {
        RoleSecurity role = new RoleSecurity("ROLE_USER");
        assertEquals("ROLE_USER", role.toString());
    }

    @Test
    void testEqualsAndHashCode() {
        RoleSecurity role1 = new RoleSecurity(1, "ROLE_USER");
        RoleSecurity role2 = new RoleSecurity(1, "ROLE_USER");
        RoleSecurity role3 = new RoleSecurity(2, "ROLE_USER");
        RoleSecurity role4 = new RoleSecurity(1, "ROLE_ADMIN");

        assertEquals(role1, role2);
        assertNotEquals(role1, role3);
        assertNotEquals(role1, role4);
        assertNotEquals(null, role1);
        assertNotEquals("string", role1);

        assertEquals(role1.hashCode(), role2.hashCode());
        assertNotEquals(role1.hashCode(), role3.hashCode());
    }
}
