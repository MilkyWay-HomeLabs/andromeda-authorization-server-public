package org.derleta.authorization.domain.builder.impl;

import org.derleta.authorization.domain.model.Role;
import org.derleta.authorization.domain.model.User;
import org.derleta.authorization.domain.model.UserRole;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

class UserRolesBuilderImplTest {

    @Test
    void testBuild() {
        long userRoleId = 1L;
        User user = new User(1L, "user", "pass", "email", new Timestamp(0), new Timestamp(0), true, false, 1);
        Role role = new Role(1, "ROLE_USER");

        UserRolesBuilderImpl builder = new UserRolesBuilderImpl();
        UserRole userRole = builder
                .userRoleId(userRoleId)
                .user(user)
                .role(role)
                .build();

        assertNotNull(userRole);
        assertEquals(userRoleId, userRole.userRoleId());
        assertEquals(user, userRole.user());
        assertEquals(role, userRole.role());
    }
}
