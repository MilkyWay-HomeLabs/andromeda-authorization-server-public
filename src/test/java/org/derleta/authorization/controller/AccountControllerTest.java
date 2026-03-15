package org.derleta.authorization.controller;

import org.derleta.authorization.controller.assembler.UserRolesModelAssembler;
import org.derleta.authorization.controller.dto.request.impl.*;
import org.derleta.authorization.controller.dto.response.AccountResponse;
import org.derleta.authorization.controller.dto.response.UserRolesResponse;
import org.derleta.authorization.domain.model.User;
import org.derleta.authorization.domain.model.UserRoles;
import org.derleta.authorization.domain.types.AccountResponseType;
import org.derleta.authorization.service.accounts.impl.AccountsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class AccountControllerTest {

    @Mock
    private AccountsServiceImpl service;

    @Mock
    private UserRolesModelAssembler userRolesModelAssembler;

    @InjectMocks
    private AccountController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegister_Success() {
        UserRegistrationRequest request = new UserRegistrationRequest("user", "pass", "email@test.com");
        AccountResponse accountResponse = new AccountResponse(true, AccountResponseType.VERIFICATION_MAIL_FROM_REGISTRATION);

        when(service.register(request)).thenReturn(accountResponse);

        ResponseEntity<AccountResponse> response = controller.register(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(accountResponse, response.getBody());
    }

    @Test
    void testRegister_Failure() {
        UserRegistrationRequest request = new UserRegistrationRequest("user", "pass", "email@test.com");
        AccountResponse accountResponse = new AccountResponse(false, AccountResponseType.BAD_REGISTRATION_REQUEST_TYPE);

        when(service.register(request)).thenReturn(accountResponse);

        ResponseEntity<AccountResponse> response = controller.register(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(accountResponse, response.getBody());
    }

    @Test
    void testGet() {
        User user = new User(1L, "user", "pass", "email@test.com", null, null, true, false, 1);
        UserRoles userRoles = new UserRoles(user, Collections.emptySet());
        UserRolesResponse userRolesResponse = new UserRolesResponse();

        when(service.get(anyString(), anyString())).thenReturn(userRoles);
        when(userRolesModelAssembler.toModel(userRoles)).thenReturn(userRolesResponse);

        ResponseEntity<UserRolesResponse> response = controller.get("user", "email@test.com");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userRolesResponse, response.getBody());
    }

    @Test
    void testConfirm_Success() {
        UserConfirmationRequest request = new UserConfirmationRequest(1L, "token1234567890");
        AccountResponse accountResponse = new AccountResponse(true, AccountResponseType.ACCOUNT_CONFIRMED);

        when(service.confirm(request)).thenReturn(accountResponse);

        ResponseEntity<AccountResponse> response = controller.confirm(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testUnlock_Success() {
        AccountResponse accountResponse = new AccountResponse(true, AccountResponseType.VERIFICATION_MAIL_FROM_UNLOCK);

        when(service.unlock(any(UserUnlockRequest.class))).thenReturn(accountResponse);

        ResponseEntity<AccountResponse> response = controller.unlock(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testResetPassword_Success() {
        AccountResponse accountResponse = new AccountResponse(true, AccountResponseType.MAIL_NEW_PASSWD_SENT);

        when(service.resetPassword(any(ResetPasswordRequest.class))).thenReturn(accountResponse);

        ResponseEntity<AccountResponse> response = controller.resetPassword("email@test.com");

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testUpdatePassword_Success() {
        ChangePasswordRequest request = new ChangePasswordRequest(1L, "email@test.com", "oldPass123", "newPass123");
        AccountResponse accountResponse = new AccountResponse(true, AccountResponseType.PASSWORD_CHANGED);

        when(service.updatePassword(request)).thenReturn(accountResponse);

        ResponseEntity<AccountResponse> response = controller.updatePassword(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
