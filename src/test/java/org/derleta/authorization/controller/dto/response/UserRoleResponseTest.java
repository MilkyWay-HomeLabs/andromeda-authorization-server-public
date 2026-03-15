package org.derleta.authorization.controller.dto.response;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserRoleResponseTest {

    @Test
    void testGettersAndSetters() {
        UserRoleResponse response = new UserRoleResponse();
        long userRoleId = 10L;
        UserResponse user = new UserResponse(1L, "user", "user@email.com");
        RoleResponse role = new RoleResponse(2, "ROLE_ADMIN");

        response.setUserRoleId(userRoleId);
        response.setUser(user);
        response.setRole(role);

        assertEquals(userRoleId, response.getUserRoleId());
        assertEquals(user, response.getUser());
        assertEquals(role, response.getRole());
    }
}
