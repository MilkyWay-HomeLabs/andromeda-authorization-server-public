package org.derleta.authorization.controller;

import org.derleta.authorization.controller.assembler.RefreshTokenModelAssembler;
import org.derleta.authorization.controller.dto.response.RefreshTokenResponse;
import org.derleta.authorization.domain.model.RefreshToken;
import org.derleta.authorization.service.token.RefreshTokenService;
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

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

class RefreshTokenControllerTest {

    @Mock
    private RefreshTokenService service;

    @Mock
    private RefreshTokenModelAssembler tokenModelAssembler;

    @Mock
    private PagedResourcesAssembler<RefreshToken> pagedResourcesAssembler;

    @InjectMocks
    private RefreshTokenController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetPage() {
        Page<RefreshToken> tokensPage = new PageImpl<>(Collections.emptyList());
        PagedModel<RefreshTokenResponse> pagedModel = PagedModel.empty();

        when(service.getPage(anyInt(), anyInt(), anyString(), anyString(), anyString(), anyString())).thenReturn(tokensPage);
        when(pagedResourcesAssembler.toModel(eq(tokensPage), eq(tokenModelAssembler))).thenReturn(pagedModel);

        ResponseEntity<PagedModel<RefreshTokenResponse>> response = controller.getPage(0, 10, "userId", "asc", "", "");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void testGetValid() {
        Page<RefreshToken> tokensPage = new PageImpl<>(Collections.emptyList());
        PagedModel<RefreshTokenResponse> pagedModel = PagedModel.empty();

        when(service.getValid(anyInt(), anyInt(), anyString(), anyString())).thenReturn(tokensPage);
        when(pagedResourcesAssembler.toModel(eq(tokensPage), eq(tokenModelAssembler))).thenReturn(pagedModel);

        ResponseEntity<PagedModel<RefreshTokenResponse>> response = controller.getValid(0, 10, "userId", "asc");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void testGet_Found() {
        RefreshToken token = new RefreshToken(1L, "token", 1L, null, 1, "jti", false);
        RefreshTokenResponse tokenResponse = new RefreshTokenResponse();

        when(service.get(1)).thenReturn(token);
        when(tokenModelAssembler.toModel(token)).thenReturn(tokenResponse);

        ResponseEntity<RefreshTokenResponse> response = controller.get(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(tokenResponse, response.getBody());
    }

    @Test
    void testGet_NotFound() {
        when(service.get(1)).thenReturn(null);

        ResponseEntity<RefreshTokenResponse> response = controller.get(1);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testAdd_Success() {
        RefreshToken token = new RefreshToken(1L, "token", 1L, null, 1, "jti", false);
        RefreshTokenResponse tokenResponse = new RefreshTokenResponse();

        when(service.createForUser(1L)).thenReturn(token);
        when(tokenModelAssembler.toModel(token)).thenReturn(tokenResponse);

        ResponseEntity<RefreshTokenResponse> response = controller.add(1L);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(tokenResponse, response.getBody());
    }

    @Test
    void testAdd_Failure() {
        when(service.createForUser(1L)).thenReturn(null);

        ResponseEntity<RefreshTokenResponse> response = controller.add(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testDelete_Success() {
        when(service.delete(1L, 1L)).thenReturn(true);

        ResponseEntity<Void> response = controller.delete(1L, 1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testDelete_NotFound() {
        when(service.delete(1L, 1L)).thenReturn(false);

        ResponseEntity<Void> response = controller.delete(1L, 1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
