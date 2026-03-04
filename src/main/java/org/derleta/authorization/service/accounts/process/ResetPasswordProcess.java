package org.derleta.authorization.service.accounts.process;

import org.derleta.authorization.controller.dto.request.Request;
import org.derleta.authorization.controller.dto.request.impl.ResetPasswordRequest;
import org.derleta.authorization.controller.dto.response.AccountResponse;
import org.derleta.authorization.domain.entity.UserEntity;
import org.derleta.authorization.domain.entity.UserEntityDecrypted;
import org.derleta.authorization.domain.types.AccountResponseType;
import org.derleta.authorization.mail.EmailService;
import org.derleta.authorization.repository.RepositoryClass;
import org.derleta.authorization.utils.MailGenerator;
import org.derleta.authorization.utils.PasswordGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * The ResetPasswordProcess class is responsible for handling operations related to resetting user passwords.
 * This includes validating reset password requests, saving updated user password details, and sending
 * confirmation emails with temporary passwords. It extends the PasswordProcess class to provide specific
 * functionality for password reset workflows.
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public final class ResetPasswordProcess extends PasswordProcess {

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public ResetPasswordProcess(
            Set<RepositoryClass> repositoryList,
            EmailService emailService,
            PasswordEncoder passwordEncoder
    ) {
        super(repositoryList, emailService);
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Sends an email to the specified user with a message containing their password.
     * The email includes a subject and a message body generated using the user's information
     * and decrypted password.
     *
     * @param userEntityDecrypted a `UserEntityDecrypted` object containing the user's information
     *                            and decrypted password to be included in the email.
     * @return an `AccountResponse` indicating the success of the email-sending operation
     * along with the response type `MAIL_NEW_PASSWD_SENT`.
     */
    public AccountResponse sendMail(final UserEntityDecrypted userEntityDecrypted) {
        UserEntity entity = userRepository.findById(userEntityDecrypted.getUserId());
        MailGenerator mailGenerator = new MailGenerator();
        String password = userEntityDecrypted.getDecryptedPassword();
        String text = mailGenerator.generatePasswordMailText(entity, password);
        String subject = MailGenerator.getPasswordSubject();
        emailService.sendEmail(entity.getEmail(), subject, text);
        return new AccountResponse(true, AccountResponseType.MAIL_NEW_PASSWD_SENT);
    }

    /**
     * Validates and processes the given request to determine the status and type of account-related action.
     * Specifically handles requests of type ResetPasswordRequest and verifies the account's existence,
     * verification status, and block status.
     *
     * @param request the request object to validate and process. Should be an instance of ResetPasswordRequest
     *                containing the email of the user whose account is being checked.
     * @return an AccountResponse indicating the success or failure of the validation, along with the corresponding
     * account-related response type.
     */
    @Override
    public AccountResponse check(Request request) {
        if (request instanceof ResetPasswordRequest(String email)) {
            UserEntity entity = userRepository.findByEmail(email);
            if (entity == null || !entity.getEmail().equalsIgnoreCase(email))
                return new AccountResponse(false, AccountResponseType.ACCOUNT_NOT_EXIST_RESET_PASSWD);
            if (userRepository.isBlocked(entity.getUserId()))
                return new AccountResponse(false, AccountResponseType.ACCOUNT_IS_BLOCKED_RESET_PASSWD);
            if (!userRepository.isVerified(entity.getUserId()))
                return new AccountResponse(false, AccountResponseType.ACCOUNT_IS_NOT_VERIFIED);
            else return new AccountResponse(true, AccountResponseType.PASSWORD_CAN_BE_GENERATED);
        }
        return new AccountResponse(false, AccountResponseType.BAD_RESET_PASSWD_REQUEST_TYPE);
    }


    @Override
    public UserEntity save(Request request) {
        if (request instanceof ResetPasswordRequest(String email)) {
            UserEntity entity = userRepository.findByEmail(email);
            if (entity == null) {
                return null;
            }
            final long userId = entity.getUserId();
            String newPassword = PasswordGenerator.generateStrongPassword();
            String encrypted = passwordEncoder.encode(newPassword);
            final int updated = userRepository.updatePassword(userId, encrypted);
            if (updated != 1) {
                return null;
            }
            UserEntity userEntity = userRepository.findById(userId);
            return new UserEntityDecrypted(userEntity, newPassword);
        }
        return null;
    }

}
