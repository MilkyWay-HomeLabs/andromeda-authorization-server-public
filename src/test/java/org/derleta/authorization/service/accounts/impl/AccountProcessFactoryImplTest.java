package org.derleta.authorization.service.accounts.impl;

import org.derleta.authorization.domain.types.AccountProcessType;
import org.derleta.authorization.service.accounts.AccountProcess;
import org.derleta.authorization.service.accounts.process.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.ObjectProvider;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountProcessFactoryImplTest {

    @Mock
    private ObjectProvider<ConfirmationTokenProcess> confirmationTokenProcessProvider;
    @Mock
    private ObjectProvider<UserRegistrationProcess> userRegistrationProcessProvider;
    @Mock
    private ObjectProvider<UnlockAccountProcess> unlockAccountProcessProvider;
    @Mock
    private ObjectProvider<ResetPasswordProcess> resetPasswordProcessProvider;
    @Mock
    private ObjectProvider<ChangePasswordProcess> changePasswordProcessProvider;

    @Mock
    private ConfirmationTokenProcess confirmationTokenProcess;
    @Mock
    private UserRegistrationProcess userRegistrationProcess;
    @Mock
    private UnlockAccountProcess unlockAccountProcess;
    @Mock
    private ResetPasswordProcess resetPasswordProcess;
    @Mock
    private ChangePasswordProcess changePasswordProcess;

    private AccountProcessFactoryImpl factory;

    @BeforeEach
    void setUp() {
        factory = new AccountProcessFactoryImpl(
                confirmationTokenProcessProvider,
                userRegistrationProcessProvider,
                unlockAccountProcessProvider,
                resetPasswordProcessProvider,
                changePasswordProcessProvider
        );
    }

    @Test
    void testCreateConfirmationToken() {
        when(confirmationTokenProcessProvider.getObject()).thenReturn(confirmationTokenProcess);
        AccountProcess result = factory.create(AccountProcessType.CONFIRMATION_TOKEN);
        assertEquals(confirmationTokenProcess, result);
    }

    @Test
    void testCreateUserRegistration() {
        when(userRegistrationProcessProvider.getObject()).thenReturn(userRegistrationProcess);
        AccountProcess result = factory.create(AccountProcessType.USER_REGISTRATION);
        assertEquals(userRegistrationProcess, result);
    }

    @Test
    void testCreateUnlockAccount() {
        when(unlockAccountProcessProvider.getObject()).thenReturn(unlockAccountProcess);
        AccountProcess result = factory.create(AccountProcessType.UNLOCK_ACCOUNT);
        assertEquals(unlockAccountProcess, result);
    }

    @Test
    void testCreateResetPassword() {
        when(resetPasswordProcessProvider.getObject()).thenReturn(resetPasswordProcess);
        AccountProcess result = factory.create(AccountProcessType.RESET_PASSWORD);
        assertEquals(resetPasswordProcess, result);
    }

    @Test
    void testCreateChangePassword() {
        when(changePasswordProcessProvider.getObject()).thenReturn(changePasswordProcess);
        AccountProcess result = factory.create(AccountProcessType.CHANGE_PASSWORD);
        assertEquals(changePasswordProcess, result);
    }
}
