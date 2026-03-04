package org.derleta.authorization.controller.assembler;

import org.derleta.authorization.controller.dto.response.AccessTokenResponse;
import org.derleta.authorization.domain.model.AccessToken;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.Link;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AccessTokenModelAssemblerTest {

    private final AccessTokenModelAssembler assembler = new AccessTokenModelAssembler();

    @Test
    void testToModel() {
        // Given
        long tokenId = 1L;
        long userId = 2L;
        Timestamp expiration = new Timestamp(System.currentTimeMillis());
        AccessToken token = new AccessToken(tokenId, "token-string", userId, expiration, 1, "jti-string", false);

        // When
        AccessTokenResponse response = assembler.toModel(token);

        // Then
        assertNotNull(response);
        assertEquals(tokenId, response.getTokenId());
        assertEquals(userId, response.getUserId());
        assertEquals(expiration, response.getExpirationDate());
        assertEquals(1, response.getVersion());
        assertEquals("jti-string", response.getJti());
        assertEquals(false, response.isRevoked());

        assertTrue(response.hasLink("self"));
        Link selfLink = response.getLink("self").get();
        assertTrue(selfLink.getHref().contains(String.valueOf(tokenId)));
    }
}
