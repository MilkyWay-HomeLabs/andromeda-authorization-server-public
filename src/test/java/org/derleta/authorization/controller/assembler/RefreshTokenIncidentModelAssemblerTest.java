package org.derleta.authorization.controller.assembler;

import org.derleta.authorization.controller.dto.response.RefreshTokenIncidentResponse;
import org.derleta.authorization.domain.entity.token.RefreshTokenIncidentEntity;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.Link;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RefreshTokenIncidentModelAssemblerTest {

    private final RefreshTokenIncidentModelAssembler assembler = new RefreshTokenIncidentModelAssembler();

    @Test
    void testToModel() {
        // Given
        RefreshTokenIncidentEntity entity = new RefreshTokenIncidentEntity();
        entity.setId(1L);
        entity.setTokenId(10L);
        entity.setUserId(100L);
        entity.setJti("jti-123");
        entity.setVersion(1);
        Timestamp now = new Timestamp(System.currentTimeMillis());
        entity.setIncidentTime(now);
        entity.setIpAddress("127.0.0.1");
        entity.setUserAgent("Mozilla/5.0");
        entity.setDescription("Incident description");

        // When
        RefreshTokenIncidentResponse response = assembler.toModel(entity);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(10L, response.getTokenId());
        assertEquals(100L, response.getUserId());
        assertEquals("jti-123", response.getJti());
        assertEquals(1, response.getVersion());
        assertEquals(now, response.getIncidentTime());
        assertEquals("127.0.0.1", response.getIpAddress());
        assertEquals("Mozilla/5.0", response.getUserAgent());
        assertEquals("Incident description", response.getDescription());

        assertTrue(response.hasLink("self"));
        Link selfLink = response.getLink("self").get();
        // The assembler uses RefreshTokenController.DEFAULT_PATH and tokenId
        assertTrue(selfLink.getHref().contains(String.valueOf(10L)));
    }
}
