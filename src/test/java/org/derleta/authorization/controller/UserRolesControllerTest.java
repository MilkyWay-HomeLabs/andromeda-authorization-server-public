package org.derleta.authorization.controller;

import org.derleta.authorization.controller.assembler.UserRolesModelAssembler;
import org.derleta.authorization.controller.dto.response.UserRolesResponse;
import org.derleta.authorization.domain.model.User;
import org.derleta.authorization.domain.model.UserRoles;
import org.derleta.authorization.service.UserRolesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class UserRolesControllerTest {

    @Mock
    private UserRolesService service;

    @Mock
    private UserRolesModelAssembler userRolesModelAssembler;

    @InjectMocks
    private UserRolesController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGet_Found() {
        User user = new User(1L, "user", "pass", "email@test.com", null, null, true, false, 1);
        UserRoles userRoles = new UserRoles(user, Collections.emptySet());
        UserRolesResponse userRolesResponse = new UserRolesResponse();

        when(service.get(anyLong(), anyString(), anyString(), anyString())).thenReturn(userRoles);
        when(userRolesModelAssembler.toModel(userRoles)).thenReturn(userRolesResponse);

        ResponseEntity<UserRolesResponse> response = controller.get(1L, "", "roleId", "asc");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userRolesResponse, response.getBody());
    }

    @Test
    void testGet_NotFound() {
        when(service.get(anyLong(), anyString(), anyString(), anyString())).thenReturn(null);

        ResponseEntity<UserRolesResponse> response = controller.get(1L, "", "roleId", "asc");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
