package org.derleta.authorization.controller;

import org.derleta.authorization.controller.assembler.UserRoleModelAssembler;
import org.derleta.authorization.controller.dto.response.UserRoleResponse;
import org.derleta.authorization.domain.model.Role;
import org.derleta.authorization.domain.model.User;
import org.derleta.authorization.domain.model.UserRole;
import org.derleta.authorization.service.UserRoleService;
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

class UserRoleControllerTest {

    @Mock
    private UserRoleService service;

    @Mock
    private UserRoleModelAssembler userRoleModelAssembler;

    @Mock
    private PagedResourcesAssembler<UserRole> pagedResourcesAssembler;

    @InjectMocks
    private UserRoleController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetPage() {
        Page<UserRole> userRolesPage = new PageImpl<>(Collections.emptyList());
        PagedModel<UserRoleResponse> pagedModel = PagedModel.empty();

        when(service.getPage(anyInt(), anyInt(), anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(userRolesPage);
        when(pagedResourcesAssembler.toModel(eq(userRolesPage), eq(userRoleModelAssembler))).thenReturn(pagedModel);

        ResponseEntity<PagedModel<UserRoleResponse>> response = controller.getPage(0, 10, "userId", "asc", "", "", "");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void testGet_Found() {
        User user = new User(1L, "user", "pass", "email@test.com", null, null, true, false, 1);
        Role role = new Role(1, "ROLE_ADMIN");
        UserRole userRole = new UserRole(1L, user, role);
        UserRoleResponse userRoleResponse = new UserRoleResponse();

        when(service.get(1L)).thenReturn(userRole);
        when(userRoleModelAssembler.toModel(userRole)).thenReturn(userRoleResponse);

        ResponseEntity<UserRoleResponse> response = controller.get(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userRoleResponse, response.getBody());
    }

    @Test
    void testGet_NotFound() {
        when(service.get(1L)).thenReturn(null);

        ResponseEntity<UserRoleResponse> response = controller.get(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testAdd_Success() {
        User user = new User(1L, "user", "pass", "email@test.com", null, null, true, false, 1);
        Role role = new Role(1, "ROLE_ADMIN");
        UserRole userRole = new UserRole(1L, user, role);
        UserRoleResponse userRoleResponse = new UserRoleResponse();

        when(service.save(1L, 1)).thenReturn(userRole);
        when(userRoleModelAssembler.toModel(userRole)).thenReturn(userRoleResponse);

        ResponseEntity<UserRoleResponse> response = controller.add(1L, 1);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(userRoleResponse, response.getBody());
    }

    @Test
    void testAdd_Failure() {
        when(service.save(1L, 1)).thenReturn(null);

        ResponseEntity<UserRoleResponse> response = controller.add(1L, 1);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testDelete_Success() {
        when(service.delete(1L, 1)).thenReturn(true);

        ResponseEntity<Void> response = controller.delete(1L, 1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testDelete_NotFound() {
        when(service.delete(1L, 1)).thenReturn(false);

        ResponseEntity<Void> response = controller.delete(1L, 1);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
