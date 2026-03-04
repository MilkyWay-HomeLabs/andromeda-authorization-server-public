package org.derleta.authorization.controller;

import org.derleta.authorization.controller.assembler.AccessTokenModelAssembler;
import org.derleta.authorization.controller.dto.response.AccessTokenResponse;
import org.derleta.authorization.domain.model.AccessToken;
import org.derleta.authorization.service.token.AccessTokenService;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

class AccessTokenControllerTest {

    @Mock
    private AccessTokenService service;

    @Mock
    private AccessTokenModelAssembler tokenModelAssembler;

    @Mock
    private PagedResourcesAssembler<AccessToken> pagedResourcesAssembler;

    @InjectMocks
    private AccessTokenController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetPage() {
        Page<AccessToken> tokensPage = new PageImpl<>(Collections.emptyList());
        PagedModel<AccessTokenResponse> pagedModel = PagedModel.empty();

        when(service.getPage(anyInt(), anyInt(), anyString(), anyString(), anyString(), anyString())).thenReturn(tokensPage);
        when(pagedResourcesAssembler.toModel(eq(tokensPage), eq(tokenModelAssembler))).thenReturn(pagedModel);

        ResponseEntity<PagedModel<AccessTokenResponse>> response = controller.getPage(0, 10, "userId", "asc", "", "");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void testGetValid() {
        Page<AccessToken> tokensPage = new PageImpl<>(Collections.emptyList());
        PagedModel<AccessTokenResponse> pagedModel = PagedModel.empty();

        when(service.getValid(anyInt(), anyInt(), anyString(), anyString())).thenReturn(tokensPage);
        when(pagedResourcesAssembler.toModel(eq(tokensPage), eq(tokenModelAssembler))).thenReturn(pagedModel);

        ResponseEntity<PagedModel<AccessTokenResponse>> response = controller.getValid(0, 10, "userId", "asc");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void testGetValid_ThrowsExceptionWhenPageNull() {
        when(service.getValid(anyInt(), anyInt(), anyString(), anyString())).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> controller.getValid(0, 10, "userId", "asc"));
    }

    @Test
    void testGet_Found() {
        AccessToken token = new AccessToken(1L, "token", 1L, null, 1, "jti", false);
        AccessTokenResponse tokenResponse = new AccessTokenResponse();

        when(service.get(1)).thenReturn(token);
        when(tokenModelAssembler.toModel(token)).thenReturn(tokenResponse);

        ResponseEntity<AccessTokenResponse> response = controller.get(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(tokenResponse, response.getBody());
    }

    @Test
    void testGet_NotFound() {
        when(service.get(1)).thenReturn(null);

        ResponseEntity<AccessTokenResponse> response = controller.get(1);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testAdd_Success() {
        AccessToken token = new AccessToken(1L, "token", 1L, null, 1, "jti", false);
        AccessTokenResponse tokenResponse = new AccessTokenResponse();

        when(service.createForUser(1L)).thenReturn(token);
        when(tokenModelAssembler.toModel(token)).thenReturn(tokenResponse);

        ResponseEntity<AccessTokenResponse> response = controller.add(1L);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(tokenResponse, response.getBody());
    }

    @Test
    void testAdd_Failure() {
        when(service.createForUser(1L)).thenReturn(null);

        ResponseEntity<AccessTokenResponse> response = controller.add(1L);

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
