package org.derleta.authorization.controller.assembler;

import org.derleta.authorization.controller.dto.response.RoleResponse;
import org.derleta.authorization.domain.model.Role;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.Link;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RoleModelAssemblerTest {

    private final RoleModelAssembler assembler = new RoleModelAssembler();

    @Test
    void testToModel() {
        // Given
        int roleId = 1;
        String roleName = "ROLE_USER";
        Role role = new Role(roleId, roleName);

        // When
        RoleResponse response = assembler.toModel(role);

        // Then
        assertNotNull(response);
        assertEquals(roleId, response.getRoleId());
        assertEquals(roleName, response.getRoleName());

        assertTrue(response.hasLink("self"));
        Link selfLink = response.getLink("self").get();
        assertTrue(selfLink.getHref().contains(String.valueOf(roleId)));
    }
}
