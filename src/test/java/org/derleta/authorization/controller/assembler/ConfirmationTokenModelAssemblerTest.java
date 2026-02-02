package org.derleta.authorization.controller.assembler;

import org.junit.jupiter.api.Test;
import org.springframework.hateoas.Link;
import org.derleta.authorization.controller.ConfirmationTokenController;
import org.derleta.authorization.domain.model.ConfirmationToken;
import org.derleta.authorization.domain.model.User;
import org.derleta.authorization.controller.dto.response.ConfirmationTokenResponse;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

class ConfirmationTokenModelAssemblerTest {

    @Test
    void toModel_shouldCorrectlyMapConfirmationTokenToResponse() {
        // Arrange
        ConfirmationToken token = new ConfirmationToken(1L, "sample-token",
                new User(2L, "John", "Doe", "john.doe@example.com"),
                new Timestamp(System.currentTimeMillis()));
        ConfirmationTokenModelAssembler assembler = new ConfirmationTokenModelAssembler();

        // Act
        ConfirmationTokenResponse response = assembler.toModel(token);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getTokenId());
        assertEquals("sample-token", response.getToken());
        assertNotNull(response.getUser());
        assertEquals("John", response.getUser().getUsername());
        assertEquals("john.doe@example.com", response.getUser().getEmail());

        Link selfLink = response.getLink("self").orElse(null);
        assertNotNull(selfLink);
        assertTrue(selfLink.getHref().contains("/" + ConfirmationTokenController.DEFAULT_PATH + "/1"));
    }

}
