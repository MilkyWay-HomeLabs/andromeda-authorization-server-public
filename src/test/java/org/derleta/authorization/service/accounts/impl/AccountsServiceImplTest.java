package org.derleta.authorization.service.accounts.impl;

import org.derleta.authorization.controller.dto.request.impl.*;
import org.derleta.authorization.controller.dto.response.AccountResponse;
import org.derleta.authorization.domain.entity.UserEntity;
import org.derleta.authorization.domain.entity.UserEntityDecrypted;
import org.derleta.authorization.domain.entity.UserRoleEntity;
import org.derleta.authorization.domain.entity.token.ConfirmationTokenEntity;
import org.derleta.authorization.domain.model.UserRoles;
import org.derleta.authorization.domain.types.AccountProcessType;
import org.derleta.authorization.domain.types.AccountResponseType;
import org.derleta.authorization.repository.impl.UserRolesRepository;
import org.derleta.authorization.service.accounts.AccountProcess;
import org.derleta.authorization.service.accounts.AccountProcessFactory;
import org.derleta.authorization.service.accounts.process.ChangePasswordProcess;
import org.derleta.authorization.service.accounts.process.ResetPasswordProcess;
import org.derleta.authorization.service.accounts.process.UserRegistrationProcess;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountsServiceImplTest {

    @Mock
    private AccountProcessFactory accountProcessFactory;
    @Mock
    private UserRolesRepository userRolesRepository;

    private AccountsServiceImpl accountsService;

    @BeforeEach
    void setUp() {
        accountsService = new AccountsServiceImpl(accountProcessFactory, userRolesRepository);
    }

    @Test
    void testRegisterSuccess() {
        UserRegistrationRequest request = mock(UserRegistrationRequest.class);
        UserRegistrationProcess process = mock(UserRegistrationProcess.class);
        UserEntity userEntity = mock(UserEntity.class);
        ConfirmationTokenEntity tokenEntity = mock(ConfirmationTokenEntity.class);
        AccountResponse successResponse = new AccountResponse(true, AccountResponseType.VERIFICATION_MAIL_FROM_REGISTRATION);

        when(accountProcessFactory.create(AccountProcessType.USER_REGISTRATION)).thenReturn(process);
        when(process.check(request)).thenReturn(new AccountResponse(true, AccountResponseType.UNIQUE_LOGIN_AND_EMAIL));
        when(process.save(request)).thenReturn(userEntity);
        when(process.getToken(userEntity)).thenReturn(tokenEntity);
        when(process.sendEmail(userEntity, tokenEntity)).thenReturn(successResponse);

        AccountResponse result = accountsService.register(request);

        assertTrue(result.isSuccess());
        assertEquals(AccountResponseType.VERIFICATION_MAIL_FROM_REGISTRATION, result.getType());
        verify(process).sendEmail(userEntity, tokenEntity);
    }

    @Test
    void testRegisterCheckFailed() {
        UserRegistrationRequest request = mock(UserRegistrationRequest.class);
        AccountProcess process = mock(AccountProcess.class);
        AccountResponse failResponse = new AccountResponse(false, AccountResponseType.EMAIL_IS_NOT_UNIQUE);

        when(accountProcessFactory.create(AccountProcessType.USER_REGISTRATION)).thenReturn(process);
        when(process.check(request)).thenReturn(failResponse);

        AccountResponse result = accountsService.register(request);

        assertFalse(result.isSuccess());
        assertEquals(AccountResponseType.EMAIL_IS_NOT_UNIQUE, result.getType());
        verify(process, never()).save(any());
    }

    @Test
    void testRegisterWrongProcessInstance() {
        UserRegistrationRequest request = mock(UserRegistrationRequest.class);
        AccountProcess process = mock(AccountProcess.class); // Not CreateConfirmationProcess

        when(accountProcessFactory.create(AccountProcessType.USER_REGISTRATION)).thenReturn(process);
        when(process.check(request)).thenReturn(new AccountResponse(true, AccountResponseType.UNIQUE_LOGIN_AND_EMAIL));
        when(process.save(request)).thenReturn(mock(UserEntity.class));

        AccountResponse result = accountsService.register(request);

        assertFalse(result.isSuccess());
        assertEquals(AccountResponseType.BAD_REGISTRATION_PROCESS_INSTANCE, result.getType());
    }

    @Test
    void testGetSuccess() {
        String username = "user";
        String email = "email@test.com";
        UserRoleEntity roleEntity = mock(UserRoleEntity.class);
        UserEntity userEntity = mock(UserEntity.class);
        when(roleEntity.getUserEntity()).thenReturn(userEntity);
        when(userEntity.getUsername()).thenReturn(username);
        when(userEntity.getEmail()).thenReturn(email);

        when(userRolesRepository.get(username, email)).thenReturn(List.of(roleEntity));

        UserRoles result = accountsService.get(username, email);

        assertNotNull(result);
        assertEquals(username, result.user().username());
    }

    @Test
    void testGetEmpty() {
        String username = "user";
        String email = "email@test.com";
        when(userRolesRepository.get(username, email)).thenReturn(Collections.emptyList());

        UserRoles result = accountsService.get(username, email);

        assertNull(result);
    }

    @Test
    void testConfirmSuccess() {
        UserConfirmationRequest request = mock(UserConfirmationRequest.class);
        AccountProcess process = mock(AccountProcess.class);
        AccountResponse successResponse = new AccountResponse(true, AccountResponseType.ACCOUNT_CONFIRMED);

        when(accountProcessFactory.create(AccountProcessType.CONFIRMATION_TOKEN)).thenReturn(process);
        when(process.check(request)).thenReturn(new AccountResponse(true, AccountResponseType.TOKEN_IS_VALID));
        when(process.update(request)).thenReturn(successResponse);

        AccountResponse result = accountsService.confirm(request);

        assertTrue(result.isSuccess());
        verify(process).update(request);
    }

    @Test
    void testUnlockSuccess() {
        UserUnlockRequest request = mock(UserUnlockRequest.class);
        UserRegistrationProcess process = mock(UserRegistrationProcess.class); // Implements CreateConfirmationProcess
        UserEntity userEntity = mock(UserEntity.class);
        ConfirmationTokenEntity tokenEntity = mock(ConfirmationTokenEntity.class);
        AccountResponse successResponse = new AccountResponse(true, AccountResponseType.VERIFICATION_MAIL_FROM_UNLOCK);

        when(accountProcessFactory.create(AccountProcessType.UNLOCK_ACCOUNT)).thenReturn(process);
        when(process.check(request)).thenReturn(new AccountResponse(true, AccountResponseType.ACCOUNT_CAN_BE_UNLOCKED));
        when(process.save(request)).thenReturn(userEntity);
        when(process.getToken(userEntity)).thenReturn(tokenEntity);
        when(process.sendEmail(userEntity, tokenEntity)).thenReturn(successResponse);

        AccountResponse result = accountsService.unlock(request);

        assertTrue(result.isSuccess());
        verify(process).sendEmail(userEntity, tokenEntity);
    }

    @Test
    void testUnlockWrongProcessInstance() {
        UserUnlockRequest request = mock(UserUnlockRequest.class);
        AccountProcess process = mock(AccountProcess.class);

        when(accountProcessFactory.create(AccountProcessType.UNLOCK_ACCOUNT)).thenReturn(process);
        when(process.check(request)).thenReturn(new AccountResponse(true, AccountResponseType.ACCOUNT_CAN_BE_UNLOCKED));

        AccountResponse result = accountsService.unlock(request);

        assertFalse(result.isSuccess());
        assertEquals(AccountResponseType.BAD_UNLOCK_PROCESS_INSTANCE, result.getType());
    }

    @Test
    void testResetPasswordSuccess() {
        ResetPasswordRequest request = mock(ResetPasswordRequest.class);
        ResetPasswordProcess process = mock(ResetPasswordProcess.class);
        UserEntityDecrypted userEntity = mock(UserEntityDecrypted.class);
        AccountResponse successResponse = new AccountResponse(true, AccountResponseType.MAIL_NEW_PASSWD_SENT);

        when(accountProcessFactory.create(AccountProcessType.RESET_PASSWORD)).thenReturn(process);
        when(process.check(request)).thenReturn(new AccountResponse(true, AccountResponseType.PASSWORD_CAN_BE_GENERATED));
        when(process.save(request)).thenReturn(userEntity);
        when(process.sendMail(userEntity)).thenReturn(successResponse);

        AccountResponse result = accountsService.resetPassword(request);

        assertTrue(result.isSuccess());
    }

    @Test
    void testResetPasswordWrongUserEntity() {
        ResetPasswordRequest request = mock(ResetPasswordRequest.class);
        ResetPasswordProcess process = mock(ResetPasswordProcess.class);
        UserEntity userEntity = mock(UserEntity.class); // Not UserEntityDecrypted

        when(accountProcessFactory.create(AccountProcessType.RESET_PASSWORD)).thenReturn(process);
        when(process.check(request)).thenReturn(new AccountResponse(true, AccountResponseType.PASSWORD_CAN_BE_GENERATED));
        when(process.save(request)).thenReturn(userEntity);

        AccountResponse result = accountsService.resetPassword(request);

        assertFalse(result.isSuccess());
        assertEquals(AccountResponseType.BAD_USER_ENTITY_INSTANCE, result.getType());
    }

    @Test
    void testUpdatePasswordSuccess() {
        ChangePasswordRequest request = new ChangePasswordRequest(1L, "email@test.com", "oldPasswd", "newPasswd");
        ChangePasswordProcess process = mock(ChangePasswordProcess.class);
        AccountResponse successResponse = new AccountResponse(true, AccountResponseType.PASSWORD_CHANGED);

        when(accountProcessFactory.create(AccountProcessType.CHANGE_PASSWORD)).thenReturn(process);
        when(process.check(request)).thenReturn(new AccountResponse(true, AccountResponseType.PASSWORD_CAN_BE_CHANGED));
        when(process.update(request)).thenReturn(successResponse);
        when(process.sendMail("email@test.com")).thenReturn(new AccountResponse(true, AccountResponseType.PASSWORD_CHANGED));

        AccountResponse result = accountsService.updatePassword(request);

        assertTrue(result.isSuccess());
        verify(process).sendMail("email@test.com");
    }

    @Test
    void testUpdatePasswordMailFailure() {
        ChangePasswordRequest request = new ChangePasswordRequest(1L, "email@test.com", "oldPasswd", "newPasswd");
        ChangePasswordProcess process = mock(ChangePasswordProcess.class);
        AccountResponse successResponse = new AccountResponse(true, AccountResponseType.PASSWORD_CHANGED);
        AccountResponse mailFailResponse = new AccountResponse(false, AccountResponseType.NULL); // Or any false response

        when(accountProcessFactory.create(AccountProcessType.CHANGE_PASSWORD)).thenReturn(process);
        when(process.check(request)).thenReturn(new AccountResponse(true, AccountResponseType.PASSWORD_CAN_BE_CHANGED));
        when(process.update(request)).thenReturn(successResponse);
        when(process.sendMail("email@test.com")).thenReturn(mailFailResponse);

        AccountResponse result = accountsService.updatePassword(request);

        assertFalse(result.isSuccess());
        assertEquals(AccountResponseType.PASSWORD_CHANGED_BUT_MAIL_NOT_SEND, result.getType());
    }
}
