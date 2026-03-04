package org.derleta.authorization.controller;

import org.derleta.authorization.controller.assembler.UserModelAssembler;
import org.derleta.authorization.controller.dto.request.impl.UserRequest;
import org.derleta.authorization.controller.dto.response.UserResponse;
import org.derleta.authorization.domain.model.User;
import org.derleta.authorization.service.UserService;
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

class UserControllerTest {

    @Mock
    private UserService service;

    @Mock
    private UserModelAssembler userModelAssembler;

    @Mock
    private PagedResourcesAssembler<User> pagedResourcesAssembler;

    @InjectMocks
    private UserController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetPage() {
        Page<User> usersPage = new PageImpl<>(Collections.emptyList());
        PagedModel<UserResponse> pagedModel = PagedModel.empty();

        when(service.getPage(anyInt(), anyInt(), anyString(), anyString(), anyString(), anyString())).thenReturn(usersPage);
        when(pagedResourcesAssembler.toModel(eq(usersPage), eq(userModelAssembler))).thenReturn(pagedModel);

        ResponseEntity<PagedModel<UserResponse>> response = controller.getPage(0, 10, "userId", "asc", "", "");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void testGet_Found() {
        User user = new User(1L, "user", "pass", "email@test.com", null, null, true, false, 1);
        UserResponse userResponse = new UserResponse(1L, "user", "email@test.com");

        when(service.get(1L)).thenReturn(user);
        when(userModelAssembler.toModel(user)).thenReturn(userResponse);

        ResponseEntity<UserResponse> response = controller.get(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userResponse, response.getBody());
    }

    @Test
    void testGet_NotFound() {
        when(service.get(1L)).thenReturn(null);

        ResponseEntity<UserResponse> response = controller.get(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testAdd() {
        UserRequest userRequest = new UserRequest("user", "pass12345", "email@test.com");
        User user = new User(1L, "user", "pass12345", "email@test.com", null, null, true, false, 1);
        UserResponse userResponse = new UserResponse(1L, "user", "email@test.com");

        when(service.save(any(User.class))).thenReturn(user);
        when(userModelAssembler.toModel(user)).thenReturn(userResponse);

        ResponseEntity<UserResponse> response = controller.add(userRequest);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(userResponse, response.getBody());
    }

    @Test
    void testUpdate_Found() {
        UserRequest userRequest = new UserRequest("user", "pass12345", "email@test.com");
        User user = new User(1L, "user", "pass12345", "email@test.com", null, null, true, false, 1);
        UserResponse userResponse = new UserResponse(1L, "user", "email@test.com");

        when(service.update(eq(1L), any(User.class))).thenReturn(user);
        when(userModelAssembler.toModel(user)).thenReturn(userResponse);

        ResponseEntity<UserResponse> response = controller.update(1L, userRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userResponse, response.getBody());
    }

    @Test
    void testUpdate_NotFound() {
        UserRequest userRequest = new UserRequest("user", "pass12345", "email@test.com");
        when(service.update(eq(1L), any(User.class))).thenReturn(null);

        ResponseEntity<UserResponse> response = controller.update(1L, userRequest);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testDelete_Success() {
        when(service.delete(1L)).thenReturn(true);

        ResponseEntity<Void> response = controller.delete(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testDelete_NotFound() {
        when(service.delete(1L)).thenReturn(false);

        ResponseEntity<Void> response = controller.delete(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
