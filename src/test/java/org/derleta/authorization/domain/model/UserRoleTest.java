package org.derleta.authorization.domain.model;

import org.junit.jupiter.api.Test;
import java.sql.Timestamp;
import static org.junit.jupiter.api.Assertions.*;

class UserRoleTest {
    @Test
    void testUserRoleRecord() {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        User user = new User(1L, "user", "pass", "email", now, now, true, false, 0);
        Role role = new Role(1, "ADMIN");
        UserRole userRole = new UserRole(100L, user, role);

        assertEquals(100L, userRole.userRoleId());
        assertEquals(user, userRole.user());
        assertEquals(role, userRole.role());

        UserRole sameUserRole = new UserRole(100L, user, role);
        assertEquals(userRole, sameUserRole);
        assertEquals(userRole.hashCode(), sameUserRole.hashCode());
        assertEquals(userRole.toString(), sameUserRole.toString());

        UserRole differentUserRole = new UserRole(101L, user, role);
        assertNotEquals(userRole, differentUserRole);
    }
}
