package org.derleta.authorization.service.accounts.process;

import org.derleta.authorization.controller.dto.request.Request;
import org.derleta.authorization.controller.dto.request.impl.UserRegistrationRequest;
import org.derleta.authorization.controller.dto.response.AccountResponse;
import org.derleta.authorization.controller.mapper.UserApiMapper;
import org.derleta.authorization.domain.entity.UserEntity;
import org.derleta.authorization.domain.types.AccountResponseType;
import org.derleta.authorization.mail.EmailService;
import org.derleta.authorization.repository.impl.UserRepository;
import org.derleta.authorization.repository.impl.UserRoleRepository;
import org.derleta.authorization.repository.impl.token.ConfirmationTokenRepository;
import org.derleta.authorization.security.util.EncryptionUtil;
import org.derleta.authorization.security.util.JwtTokenUtil;
import org.derleta.authorization.service.accounts.AccountProcess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * The UserRegistrationProcess class handles the process of user registration,
 * including validation of registration data, storing user information, and assigning roles to users.
 * This class extends CreateConfirmationProcess and implements AccountProcess.
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public non-sealed class UserRegistrationProcess extends CreateConfirmationProcess implements AccountProcess {

    private static final int USER_ROLE_ID = 1;

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;

    @Autowired
    public UserRegistrationProcess(
            EmailService emailService,
            EncryptionUtil encryptionUtil,
            ConfirmationTokenRepository confirmationTokenRepository,
            JwtTokenUtil jwtTokenUtil,
            UserRepository userRepository,
            UserRoleRepository userRoleRepository
    ) {
        super(emailService, encryptionUtil, confirmationTokenRepository, jwtTokenUtil);
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
    }

    /**
     * Validates the provided registration request to ensure the uniqueness of the email and username.
     * Determines whether the registration request is valid or not based on specific criteria.
     *
     * @param request the request object containing user registration details; must be an instance of UserRegistrationRequest
     * @return an AccountResponse object indicating success or failure, along with the appropriate response type:
     * UNIQUE_LOGIN_AND_EMAIL if the email and username are both unique,
     * EMAIL_IS_NOT_UNIQUE if the email is already in use,
     * LOGIN_IS_NOT_UNIQUE if the username is already in use,
     * BAD_REGISTRATION_REQUEST_TYPE if the request is not a valid UserRegistrationRequest.
     */
    @Override
    public AccountResponse check(Request request) {
        if (request instanceof UserRegistrationRequest instance) {
            boolean emailStatus = userRepository.isEmailExist(instance.email());
            if (emailStatus) return new AccountResponse(false, AccountResponseType.EMAIL_IS_NOT_UNIQUE);

            boolean loginStatus = userRepository.isLoginExist(instance.username());
            if (loginStatus) return new AccountResponse(false, AccountResponseType.LOGIN_IS_NOT_UNIQUE);

            return new AccountResponse(true, AccountResponseType.UNIQUE_LOGIN_AND_EMAIL);
        }
        return new AccountResponse(false, AccountResponseType.BAD_REGISTRATION_REQUEST_TYPE);
    }

    @Override
    @Transactional
    public UserEntity save(Request request) {
        if (request instanceof UserRegistrationRequest instance) {
            var entity = UserApiMapper.toUserEntity(instance);

            Long userId = userRepository.save(entity);
            if (userId == null) {
                throw new RuntimeException("Failed to save user entity");
            }

            saveUserRoleToDatabase(userId);
            return userRepository.findById(userId);
        }
        throw new RuntimeException("Bad instance of Request parameter");
    }

    /**
     * Associates a user with a predefined role by saving this relationship
     * into the `user_roles` table in the database.
     *
     * @param userId the unique identifier of the user to whom the role will be assigned
     */
    private void saveUserRoleToDatabase(Long userId) {
        userRoleRepository.save(userId, USER_ROLE_ID);
    }

}
