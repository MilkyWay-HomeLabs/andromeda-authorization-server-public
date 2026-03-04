package org.derleta.authorization.controller.assembler;

import org.derleta.authorization.controller.dto.response.RoleResponse;
import org.derleta.authorization.controller.dto.response.UserResponse;
import org.derleta.authorization.controller.dto.response.UserRoleResponse;
import org.derleta.authorization.domain.model.Role;
import org.derleta.authorization.domain.model.User;
import org.derleta.authorization.domain.model.UserRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.hateoas.Link;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserRoleModelAssemblerTest {

    @Mock
    private UserModelAssembler userModelAssembler;

    @Mock
    private RoleModelAssembler roleModelAssembler;

    @InjectMocks
    private UserRoleModelAssembler assembler;

    @Test
    void testToModel() {
        // Given
        User user = new User(1L, "user", "pass", "email", null, null, true, false, 1);
        Role role = new Role(10, "ROLE_ADMIN");
        UserRole userRole = new UserRole(100L, user, role);

        UserResponse userResponse = new UserResponse();
        userResponse.setUserId(1L);
        when(userModelAssembler.toModel(user)).thenReturn(userResponse);

        RoleResponse roleResponse = new RoleResponse();
        roleResponse.setRoleId(10);
        when(roleModelAssembler.toModel(role)).thenReturn(roleResponse);

        // When
        UserRoleResponse response = assembler.toModel(userRole);

        // Then
        assertNotNull(response);
        assertEquals(100L, response.getUserRoleId());
        assertEquals(userResponse, response.getUser());
        assertEquals(roleResponse, response.getRole());

        assertTrue(response.hasLink("self"));
        Link selfLink = response.getLink("self").get();
        assertTrue(selfLink.getHref().contains(String.valueOf(100L)));
    }
}
