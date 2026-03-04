package org.derleta.authorization.controller.assembler;

import org.derleta.authorization.controller.dto.response.UserResponse;
import org.derleta.authorization.domain.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.Link;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserModelAssemblerTest {

    private final UserModelAssembler assembler = new UserModelAssembler();

    @Test
    void testToModel() {
        // Given
        long userId = 1L;
        String username = "testuser";
        String email = "test@example.com";
        User user = new User(userId, username, "password", email, null, null, true, false, 1);

        // When
        UserResponse response = assembler.toModel(user);

        // Then
        assertNotNull(response);
        assertEquals(userId, response.getUserId());
        assertEquals(username, response.getUsername());
        assertEquals(email, response.getEmail());

        assertTrue(response.hasLink("self"));
        Link selfLink = response.getLink("self").get();
        assertTrue(selfLink.getHref().contains(String.valueOf(userId)));
    }
}
