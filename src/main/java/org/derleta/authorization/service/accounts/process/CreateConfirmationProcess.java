package org.derleta.authorization.service.accounts.process;

import org.derleta.authorization.controller.dto.response.AccountResponse;
import org.derleta.authorization.domain.entity.UserEntity;
import org.derleta.authorization.domain.entity.token.ConfirmationTokenEntity;
import org.derleta.authorization.domain.types.AccountResponseType;
import org.derleta.authorization.mail.EmailService;
import org.derleta.authorization.repository.impl.token.ConfirmationTokenRepository;
import org.derleta.authorization.security.util.EncryptionUtil;
import org.derleta.authorization.security.util.JwtTokenUtil;
import org.derleta.authorization.utils.MailGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Abstract class representing a process for creating confirmation tokens and sending emails.
 * This class is sealed and allows specific implementations to handle different types
 * of confirmation processes such as UnlockAccountProcess and UserRegistrationProcess.
 */
@Service
public abstract sealed class CreateConfirmationProcess permits UnlockAccountProcess, UserRegistrationProcess {

    private final EmailService emailService;
    private final EncryptionUtil encryptionUtil;
    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final JwtTokenUtil jwtTokenUtil;

    @Autowired
    public CreateConfirmationProcess(EmailService emailService,
                                     EncryptionUtil encryptionUtil,
                                     ConfirmationTokenRepository confirmationTokenRepository,
                                     JwtTokenUtil jwtTokenUtil) {
        this.emailService = emailService;
        this.encryptionUtil = encryptionUtil;
        this.confirmationTokenRepository = confirmationTokenRepository;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    public ConfirmationTokenEntity getToken(final UserEntity userEntity) {
        if (userEntity == null) return null;

        var token = jwtTokenUtil.generateConfirmationToken(userEntity.getUserId(), userEntity.getTokenVersion());
        String encryptedToken = encryptionUtil.encrypt(token.token());
        java.sql.Timestamp expiresAt = java.sql.Timestamp.from(token.expiresAt().toInstant());

        int saved = confirmationTokenRepository.save(
                userEntity.getUserId(),
                encryptedToken,
                expiresAt,
                userEntity.getTokenVersion(),
                token.jti(),
                false);

        if (saved <= 0) return null;

        return confirmationTokenRepository.findByJti(token.jti())
                .filter(ConfirmationTokenEntity.class::isInstance)
                .map(ConfirmationTokenEntity.class::cast)
                .orElse(null);
    }

    /**
     * Sends a verification email to the specified user using the provided confirmation token.
     *
     * @param userEntity              the user entity containing user details such as email
     * @param confirmationTokenEntity the confirmation token entity containing token details
     * @return an AccountResponse indicating the result of the email sending process
     */
    public AccountResponse sendEmail(UserEntity userEntity, ConfirmationTokenEntity confirmationTokenEntity) {
        MailGenerator mailGenerator = new MailGenerator();
        String text = mailGenerator.generateVerificationMailText(userEntity, confirmationTokenEntity);
        String subject = MailGenerator.getVerificationSubject();
        emailService.sendEmail(userEntity.getEmail(), subject, text);
        return getResponse(this);
    }

    /**
     * Generates an AccountResponse based on the type of CreateConfirmationProcess.
     *
     * @param createConfirmationProcess the process type that determines the response content
     * @return an AccountResponse object containing success status and response type
     */
    private AccountResponse getResponse(CreateConfirmationProcess createConfirmationProcess) {
        return switch (createConfirmationProcess) {
            case UserRegistrationProcess ignored ->
                    new AccountResponse(true, AccountResponseType.VERIFICATION_MAIL_FROM_REGISTRATION);
            case UnlockAccountProcess ignored ->
                    new AccountResponse(true, AccountResponseType.VERIFICATION_MAIL_FROM_UNLOCK);
        };
    }

}
