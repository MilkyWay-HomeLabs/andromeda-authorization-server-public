package org.derleta.authorization.service.accounts.process;

import org.derleta.authorization.controller.dto.request.Request;
import org.derleta.authorization.controller.dto.request.impl.UserUnlockRequest;
import org.derleta.authorization.controller.dto.response.AccountResponse;
import org.derleta.authorization.domain.entity.UserEntity;
import org.derleta.authorization.domain.types.AccountResponseType;
import org.derleta.authorization.mail.EmailService;
import org.derleta.authorization.repository.impl.UserRepository;
import org.derleta.authorization.repository.impl.token.ConfirmationTokenRepository;
import org.derleta.authorization.security.util.EncryptionUtil;
import org.derleta.authorization.security.util.JwtTokenUtil;
import org.derleta.authorization.service.accounts.AccountProcess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Service class that handles the process of unlocking a user's account.
 * Extends CreateConfirmationProcess and implements AccountProcess to provide
 * functionality for validating and processing unlock requests, as well as
 * updating user account status.
 * <p>
 * This class relies on UserRepository for user-related database operations
 * and EmailService for email notifications. Confirmation tokens are managed
 * using ConfirmationTokenRepository, inherited from CreateConfirmationProcess.
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public final class UnlockAccountProcess extends CreateConfirmationProcess implements AccountProcess {

    private final UserRepository userRepository;

    @Autowired
    public UnlockAccountProcess(
            EmailService emailService,
            EncryptionUtil encryptionUtil,
            ConfirmationTokenRepository confirmationTokenRepository,
            JwtTokenUtil jwtTokenUtil,
            UserRepository userRepository
    ) {
        super(emailService, encryptionUtil, confirmationTokenRepository, jwtTokenUtil);
        this.userRepository = userRepository;
    }

    @Override
    public AccountResponse check(final Request request) {
        if (request instanceof UserUnlockRequest(Long userId)) {
            UserEntity entity = userRepository.findById(userId);
            if (entity == null || entity.getUserId() != userId) {
                return new AccountResponse(false, AccountResponseType.ACCOUNT_NOT_EXIST_UNLOCK_ACCOUNT);
            }

            boolean isVerified = userRepository.isVerified(userId);
            boolean isBlocked = userRepository.isBlocked(userId);

            if (isVerified && !isBlocked) {
                return new AccountResponse(false, AccountResponseType.ACCOUNT_VERIFIED_AND_NOT_BLOCKED);
            }
            return new AccountResponse(true, AccountResponseType.ACCOUNT_CAN_BE_UNLOCKED);
        }
        return new AccountResponse(false, AccountResponseType.BAD_UNLOCK_REQUEST_TYPE);
    }


    /**
     * Updates the status of a user in the database and retrieves the updated user entity.
     *
     * @param request the request object must be an instance of UserUnlockRequest
     *                containing the user ID to update and retrieve
     * @return the corresponding UserEntity object after the update if the request is valid,
     * otherwise null
     */
    @Override
    public UserEntity save(final Request request) {
        if (request instanceof UserUnlockRequest(Long userId)) {
            userRepository.updateStatus(userId, false, false);
            return userRepository.findById(userId);
        }
        return null;
    }

}
