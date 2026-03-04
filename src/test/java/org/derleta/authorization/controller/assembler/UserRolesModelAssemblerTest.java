package org.derleta.authorization.controller.assembler;

import org.derleta.authorization.controller.dto.response.RoleResponse;
import org.derleta.authorization.controller.dto.response.UserResponse;
import org.derleta.authorization.controller.dto.response.UserRolesResponse;
import org.derleta.authorization.domain.model.Role;
import org.derleta.authorization.domain.model.User;
import org.derleta.authorization.domain.model.UserRoles;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.hateoas.Link;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserRolesModelAssemblerTest {

    @Mock
    private UserModelAssembler userModelAssembler;

    @Mock
    private RoleModelAssembler roleModelAssembler;

    @InjectMocks
    private UserRolesModelAssembler assembler;

    @Test
    void testToModel() {
        // Given
        User user = new User(1L, "user", "pass", "email", null, null, true, false, 1);
        Role role1 = new Role(10, "ROLE_ADMIN");
        Role role2 = new Role(11, "ROLE_USER");
        UserRoles userRoles = new UserRoles(user, Set.of(role1, role2));

        UserResponse userResponse = new UserResponse();
        userResponse.setUserId(1L);
        when(userModelAssembler.toModel(user)).thenReturn(userResponse);

        RoleResponse roleResponse1 = new RoleResponse();
        roleResponse1.setRoleId(10);
        roleResponse1.setRoleName("ROLE_ADMIN");
        roleResponse1.add(Link.of("http://localhost/roles/10", "self"));
        when(roleModelAssembler.toModel(role1)).thenReturn(roleResponse1);

        RoleResponse roleResponse2 = new RoleResponse();
        roleResponse2.setRoleId(11);
        roleResponse2.setRoleName("ROLE_USER");
        roleResponse2.add(Link.of("http://localhost/roles/11", "self"));
        when(roleModelAssembler.toModel(role2)).thenReturn(roleResponse2);

        // When
        UserRolesResponse response = assembler.toModel(userRoles);

        // Then
        assertNotNull(response);
        assertEquals(userResponse, response.getUser());
        assertEquals(2, response.getRoles().size());
        assertTrue(response.getRoles().stream().anyMatch(r -> r.getRoleId() == 10));
        assertTrue(response.getRoles().stream().anyMatch(r -> r.getRoleId() == 11));

        assertTrue(response.hasLink("self"));
        Link selfLink = response.getLink("self").get();
        assertTrue(selfLink.getHref().contains(String.valueOf(1L)));
    }
}
