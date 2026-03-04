package org.derleta.authorization.controller.dto.response;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RoleResponseTest {

    @Test
    void testGettersAndSetters() {
        RoleResponse response = new RoleResponse();
        int roleId = 1;
        String roleName = "ROLE_USER";

        response.setRoleId(roleId);
        response.setRoleName(roleName);

        assertEquals(roleId, response.getRoleId());
        assertEquals(roleName, response.getRoleName());
    }

    @Test
    void testConstructor() {
        int roleId = 2;
        String roleName = "ROLE_ADMIN";
        RoleResponse response = new RoleResponse(roleId, roleName);

        assertEquals(roleId, response.getRoleId());
        assertEquals(roleName, response.getRoleName());
    }
}
