package org.derleta.authorization.service.accounts;

import org.derleta.authorization.controller.dto.request.*;
import org.derleta.authorization.controller.dto.response.AccountResponse;
import org.derleta.authorization.domain.model.UserRoles;

/**
 * Service interface for managing and handling accounts.
 * Provides operations for user registration, confirmation, unlocking,
 * password management, and retrieving user roles.
 */
public interface AccountsService {

    AccountResponse register(final UserRegistrationRequest request);

    AccountResponse confirm(final UserConfirmationRequest request);

    AccountResponse unlock(final UserUnlockRequest request);

    AccountResponse resetPassword(final ResetPasswordRequest request);

    UserRoles get(final String username, final String email);

    AccountResponse updatePassword(final ChangePasswordRequest changePasswordRequest);

}
