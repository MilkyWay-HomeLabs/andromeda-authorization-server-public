package org.derleta.authorization.controller.dto.response;

import org.junit.jupiter.api.Test;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

class UserRolesResponseTest {

    @Test
    void testGettersAndSetters() {
        UserRolesResponse response = new UserRolesResponse();
        UserResponse user = new UserResponse(1L, "user", "email");
        RoleResponse role1 = new RoleResponse(1, "ROLE_USER");
        RoleResponse role2 = new RoleResponse(2, "ROLE_ADMIN");
        Set<RoleResponse> roles = Set.of(role1, role2);

        response.setUser(user);
        response.setRoles(roles);

        assertEquals(user, response.getUser());
        assertEquals(roles, response.getRoles());
    }
}
