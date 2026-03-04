package org.derleta.authorization.service.accounts.process;

import org.derleta.authorization.controller.dto.request.Request;
import org.derleta.authorization.controller.dto.request.impl.ChangePasswordRequest;
import org.derleta.authorization.controller.dto.response.AccountResponse;
import org.derleta.authorization.domain.entity.UserEntity;
import org.derleta.authorization.domain.types.AccountResponseType;
import org.derleta.authorization.mail.EmailService;
import org.derleta.authorization.repository.RepositoryClass;
import org.derleta.authorization.utils.MailGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;


@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public final class ChangePasswordProcess extends PasswordProcess {

    private final PasswordEncoder encoder;

    /**
     * Constructor for the ChangePasswordProcess class.
     *
     * @param repositoryList a set of repository instances required for handling
     *                       password change processes.
     * @param emailService   the service used for handling email notifications.
     * @param encoder        the password encoder utilized for securing passwords.
     */
    @Autowired
    public ChangePasswordProcess(Set<RepositoryClass> repositoryList, EmailService emailService, PasswordEncoder encoder) {
        super(repositoryList, emailService);
        this.encoder = encoder;
    }


    @Override
    public AccountResponse check(final Request request) {
        if (!(request instanceof ChangePasswordRequest instance)) {
            return new AccountResponse(false, AccountResponseType.BAD_RESET_PASSWD_REQUEST_TYPE);
        }

        final String email = instance.email();
        UserEntity entity = userRepository.findByEmail(email);
        if (entity == null || entity.getEmail() == null || !entity.getEmail().equalsIgnoreCase(email)) {
            return new AccountResponse(false, AccountResponseType.EMAIL_NOT_EXIST_CHANGE_PASSWD);
        }
        if (userRepository.isBlocked(entity.getUserId())) {
            return new AccountResponse(false, AccountResponseType.ACCOUNT_IS_BLOCKED_CHANGE_PASSWD);
        }
        if (!encoder.matches(instance.actualPassword(), entity.getPassword())) {
            return new AccountResponse(false, AccountResponseType.BAD_ACTUAL_PASSWORD_CHANGE_PASSWD);
        }
        return new AccountResponse(true, AccountResponseType.PASSWORD_CAN_BE_CHANGED);
    }

    @Override
    public AccountResponse update(final Request request) {
        if (!(request instanceof ChangePasswordRequest instance)) {
            return new AccountResponse(false, AccountResponseType.PASSWORD_NOT_CHANGED);
        }

        final String email = instance.email();
        UserEntity entity = userRepository.findByEmail(email);
        if (entity == null) {
            return new AccountResponse(false, AccountResponseType.EMAIL_NOT_EXIST_CHANGE_PASSWD);
        }

        final long userId = entity.getUserId();
        String encrypted = encoder.encode(instance.newPassword());

        final int updated = userRepository.updatePassword(userId, encrypted);
        if (updated != 1) {
            return new AccountResponse(false, AccountResponseType.PASSWORD_NOT_CHANGED);
        }
        return new AccountResponse(true, AccountResponseType.PASSWORD_CHANGED);
    }

    /**
     * Sends an email to the specified address containing information regarding password change.
     *
     * @param emailAddress the email address to which the email should be sent
     * @return an AccountResponse indicating the success of the email sending operation,
     * with the type set to AccountResponseType.MAIL_NEW_PASSWD_SENT
     */
    public AccountResponse sendMail(final String emailAddress) {
        MailGenerator mailGenerator = new MailGenerator();
        String text = mailGenerator.generateChangePasswdInfoMailText();
        String subject = MailGenerator.getPasswordSubject();
        emailService.sendEmail(emailAddress, subject, text);
        return new AccountResponse(true, AccountResponseType.MAIL_NEW_PASSWD_SENT);
    }

}
