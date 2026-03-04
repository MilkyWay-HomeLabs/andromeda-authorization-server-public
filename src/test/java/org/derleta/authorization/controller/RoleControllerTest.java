package org.derleta.authorization.controller;

import org.derleta.authorization.controller.assembler.RoleModelAssembler;
import org.derleta.authorization.controller.dto.response.RoleResponse;
import org.derleta.authorization.domain.model.Role;
import org.derleta.authorization.service.RoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

class RoleControllerTest {

    @Mock
    private RoleService service;

    @Mock
    private RoleModelAssembler roleModelAssembler;

    @Mock
    private PagedResourcesAssembler<Role> pagedResourcesAssembler;

    @InjectMocks
    private RoleController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetList() {
        Role role = new Role(1, "ROLE_ADMIN");
        RoleResponse roleResponse = new RoleResponse();
        when(service.getList(anyString())).thenReturn(Set.of(role));
        when(roleModelAssembler.toModel(role)).thenReturn(roleResponse);

        ResponseEntity<CollectionModel<RoleResponse>> response = controller.getList("");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void testGetPage() {
        Page<Role> rolesPage = new PageImpl<>(Collections.emptyList());
        PagedModel<RoleResponse> pagedModel = PagedModel.empty();

        when(service.getPage(anyInt(), anyInt(), anyString(), anyString(), anyString())).thenReturn(rolesPage);
        when(pagedResourcesAssembler.toModel(eq(rolesPage), eq(roleModelAssembler))).thenReturn(pagedModel);

        ResponseEntity<PagedModel<RoleResponse>> response = controller.getPage(0, 10, "roleId", "asc", "");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void testGet_Found() {
        Role role = new Role(1, "ROLE_ADMIN");
        RoleResponse roleResponse = new RoleResponse();

        when(service.get(1)).thenReturn(role);
        when(roleModelAssembler.toModel(role)).thenReturn(roleResponse);

        ResponseEntity<RoleResponse> response = controller.get(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(roleResponse, response.getBody());
    }

    @Test
    void testGet_NotFound() {
        when(service.get(1)).thenReturn(null);

        ResponseEntity<RoleResponse> response = controller.get(1);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testAdd() {
        Role role = new Role(1, "ROLE_ADMIN");
        RoleResponse roleResponse = new RoleResponse();

        when(service.save(any(Role.class))).thenReturn(role);
        when(roleModelAssembler.toModel(role)).thenReturn(roleResponse);

        ResponseEntity<RoleResponse> response = controller.add(role);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(roleResponse, response.getBody());
    }

    @Test
    void testUpdate_Found() {
        Role role = new Role(1, "ROLE_ADMIN");
        RoleResponse roleResponse = new RoleResponse();

        when(service.update(eq(1), any(Role.class))).thenReturn(role);
        when(roleModelAssembler.toModel(role)).thenReturn(roleResponse);

        ResponseEntity<RoleResponse> response = controller.update(1, role);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(roleResponse, response.getBody());
    }

    @Test
    void testUpdate_NotFound() {
        Role role = new Role(1, "ROLE_ADMIN");
        when(service.update(eq(1), any(Role.class))).thenReturn(null);

        ResponseEntity<RoleResponse> response = controller.update(1, role);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testDelete_Success() {
        when(service.delete(1)).thenReturn(true);

        ResponseEntity<Void> response = controller.delete(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testDelete_NotFound() {
        when(service.delete(1)).thenReturn(false);

        ResponseEntity<Void> response = controller.delete(1);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
