package org.derleta.authorization.controller;

import org.derleta.authorization.controller.assembler.RefreshTokenIncidentModelAssembler;
import org.derleta.authorization.controller.dto.response.RefreshTokenIncidentResponse;
import org.derleta.authorization.domain.entity.token.RefreshTokenIncidentEntity;
import org.derleta.authorization.service.token.RefreshTokenIncidentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

class RefreshTokenIncidentControllerTest {

    @Mock
    private RefreshTokenIncidentService service;

    @Mock
    private RefreshTokenIncidentModelAssembler incidentModelAssembler;

    @Mock
    private PagedResourcesAssembler<RefreshTokenIncidentEntity> pagedResourcesAssembler;

    @InjectMocks
    private RefreshTokenIncidentController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetPage() {
        Page<RefreshTokenIncidentEntity> incidentsPage = new PageImpl<>(Collections.emptyList());
        PagedModel<RefreshTokenIncidentResponse> pagedModel = PagedModel.empty();

        when(service.getPage(anyInt(), anyInt())).thenReturn(incidentsPage);
        when(pagedResourcesAssembler.toModel(eq(incidentsPage), eq(incidentModelAssembler))).thenReturn(pagedModel);

        ResponseEntity<PagedModel<RefreshTokenIncidentResponse>> response = controller.getPage(0, 50);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void testGetById_Found() {
        RefreshTokenIncidentEntity entity = new RefreshTokenIncidentEntity();
        RefreshTokenIncidentResponse incidentResponse = new RefreshTokenIncidentResponse();

        when(service.findById(1L)).thenReturn(Optional.of(entity));
        when(incidentModelAssembler.toModel(entity)).thenReturn(incidentResponse);

        ResponseEntity<RefreshTokenIncidentResponse> response = controller.getById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(incidentResponse, response.getBody());
    }

    @Test
    void testGetById_NotFound() {
        when(service.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<RefreshTokenIncidentResponse> response = controller.getById(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testGetFrom() {
        Page<RefreshTokenIncidentEntity> incidentsPage = new PageImpl<>(Collections.emptyList());
        PagedModel<RefreshTokenIncidentResponse> pagedModel = PagedModel.empty();
        Instant from = Instant.now();

        when(service.getFrom(eq(from), anyInt())).thenReturn(incidentsPage);
        when(pagedResourcesAssembler.toModel(eq(incidentsPage), eq(incidentModelAssembler))).thenReturn(pagedModel);

        ResponseEntity<PagedModel<RefreshTokenIncidentResponse>> response = controller.getFrom(from, 1000);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void testGetLast24h() {
        Page<RefreshTokenIncidentEntity> incidentsPage = new PageImpl<>(Collections.emptyList());
        PagedModel<RefreshTokenIncidentResponse> pagedModel = PagedModel.empty();

        when(service.getLast24h(anyInt())).thenReturn(incidentsPage);
        when(pagedResourcesAssembler.toModel(eq(incidentsPage), eq(incidentModelAssembler))).thenReturn(pagedModel);

        ResponseEntity<PagedModel<RefreshTokenIncidentResponse>> response = controller.getLast24h(1000);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void testGetFromTimestamp() {
        Page<RefreshTokenIncidentEntity> incidentsPage = new PageImpl<>(Collections.emptyList());
        PagedModel<RefreshTokenIncidentResponse> pagedModel = PagedModel.empty();
        Timestamp from = Timestamp.from(Instant.now());

        when(service.getFrom(eq(from.toInstant()), anyInt())).thenReturn(incidentsPage);
        when(pagedResourcesAssembler.toModel(eq(incidentsPage), eq(incidentModelAssembler))).thenReturn(pagedModel);

        ResponseEntity<PagedModel<RefreshTokenIncidentResponse>> response = controller.getFromTimestamp(from, 1000);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }
}
