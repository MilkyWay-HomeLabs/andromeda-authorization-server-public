package org.derleta.authorization.domain.model;

import org.junit.jupiter.api.Test;
import java.sql.Timestamp;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

class UserRolesTest {
    @Test
    void testUserRolesRecord() {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        User user = new User(1L, "user", "pass", "email", now, now, true, false, 0);
        Role role1 = new Role(1, "ADMIN");
        Role role2 = new Role(2, "USER");
        Set<Role> roles = Set.of(role1, role2);
        UserRoles userRoles = new UserRoles(user, roles);

        assertEquals(user, userRoles.user());
        assertEquals(roles, userRoles.roles());

        UserRoles sameUserRoles = new UserRoles(user, roles);
        assertEquals(userRoles, sameUserRoles);
        assertEquals(userRoles.hashCode(), sameUserRoles.hashCode());
        assertEquals(userRoles.toString(), sameUserRoles.toString());

        UserRoles differentUserRoles = new UserRoles(user, Set.of(role1));
        assertNotEquals(userRoles, differentUserRoles);
    }
}
