package org.derleta.authorization.service.accounts.impl;

import org.derleta.authorization.domain.types.AccountProcessType;
import org.derleta.authorization.service.accounts.AccountProcess;
import org.derleta.authorization.service.accounts.AccountProcessFactory;
import org.derleta.authorization.service.accounts.process.*;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

@Component
public class AccountProcessFactoryImpl implements AccountProcessFactory {

    private final ObjectProvider<ConfirmationTokenProcess> confirmationTokenProcessProvider;
    private final ObjectProvider<UserRegistrationProcess> userRegistrationProcessProvider;
    private final ObjectProvider<UnlockAccountProcess> unlockAccountProcessProvider;
    private final ObjectProvider<ResetPasswordProcess> resetPasswordProcessProvider;
    private final ObjectProvider<ChangePasswordProcess> changePasswordProcessProvider;

    public AccountProcessFactoryImpl(
            ObjectProvider<ConfirmationTokenProcess> confirmationTokenProcessProvider,
            ObjectProvider<UserRegistrationProcess> userRegistrationProcessProvider,
            ObjectProvider<UnlockAccountProcess> unlockAccountProcessProvider,
            ObjectProvider<ResetPasswordProcess> resetPasswordProcessProvider,
            ObjectProvider<ChangePasswordProcess> changePasswordProcessProvider
    ) {
        this.confirmationTokenProcessProvider = confirmationTokenProcessProvider;
        this.userRegistrationProcessProvider = userRegistrationProcessProvider;
        this.unlockAccountProcessProvider = unlockAccountProcessProvider;
        this.resetPasswordProcessProvider = resetPasswordProcessProvider;
        this.changePasswordProcessProvider = changePasswordProcessProvider;
    }

    @Override
    public AccountProcess create(AccountProcessType process) {
        return switch (process) {
            case CONFIRMATION_TOKEN -> confirmationTokenProcessProvider.getObject();
            case USER_REGISTRATION -> userRegistrationProcessProvider.getObject();
            case UNLOCK_ACCOUNT -> unlockAccountProcessProvider.getObject();
            case RESET_PASSWORD -> resetPasswordProcessProvider.getObject();
            case CHANGE_PASSWORD -> changePasswordProcessProvider.getObject();
        };
    }

}
