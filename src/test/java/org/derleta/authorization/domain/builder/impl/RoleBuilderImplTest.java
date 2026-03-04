package org.derleta.authorization.domain.builder.impl;

import org.derleta.authorization.domain.model.Role;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RoleBuilderImplTest {

    @Test
    void testBuild() {
        int roleId = 1;
        String roleName = "ROLE_USER";

        RoleBuilderImpl builder = new RoleBuilderImpl();
        Role role = builder
                .roleId(roleId)
                .roleName(roleName)
                .build();

        assertNotNull(role);
        assertEquals(roleId, role.roleId());
        assertEquals(roleName, role.roleName());
    }
}
