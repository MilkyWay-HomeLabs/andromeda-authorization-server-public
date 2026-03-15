package org.derleta.authorization.service.accounts.process;

import org.derleta.authorization.controller.dto.response.AccountResponse;
import org.derleta.authorization.domain.entity.UserEntity;
import org.derleta.authorization.domain.entity.token.ConfirmationTokenEntity;
import org.derleta.authorization.domain.model.ConfirmationTokenResult;
import org.derleta.authorization.domain.types.AccountResponseType;
import org.derleta.authorization.mail.EmailService;
import org.derleta.authorization.repository.impl.UserRepository;
import org.derleta.authorization.repository.impl.UserRoleRepository;
import org.derleta.authorization.repository.impl.token.ConfirmationTokenRepository;
import org.derleta.authorization.security.util.EncryptionUtil;
import org.derleta.authorization.security.util.JwtTokenUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateConfirmationProcessTest {

    @Mock
    private EmailService emailService;

    @Mock
    private EncryptionUtil encryptionUtil;

    @Mock
    private ConfirmationTokenRepository confirmationTokenRepository;

    @Mock
    private JwtTokenUtil jwtTokenUtil;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserRoleRepository userRoleRepository;

    @Test
    void testGetToken_Success() {
        UserRegistrationProcess process = new UserRegistrationProcess(
                emailService, encryptionUtil, confirmationTokenRepository, jwtTokenUtil, userRepository, userRoleRepository
        );

        UserEntity user = new UserEntity();
        user.setUserId(1L);
        user.setTokenVersion(2);

        ConfirmationTokenResult generatedToken = new ConfirmationTokenResult("rawToken", "jti", new Date(), new Date());
        ConfirmationTokenEntity tokenEntity = new ConfirmationTokenEntity();

        when(jwtTokenUtil.generateConfirmationToken(1L, 2)).thenReturn(generatedToken);
        when(encryptionUtil.encrypt("rawToken")).thenReturn("encryptedToken");
        when(confirmationTokenRepository.save(eq(1L), eq("encryptedToken"), any(Timestamp.class), eq(2), eq("jti"), eq(false))).thenReturn(1);
        when(confirmationTokenRepository.findByJti("jti")).thenReturn(Optional.of(tokenEntity));

        ConfirmationTokenEntity result = process.getToken(user);

        assertNotNull(result);
        assertEquals(tokenEntity, result);
    }

    @Test
    void testGetToken_NullUser() {
        UserRegistrationProcess process = new UserRegistrationProcess(
                emailService, encryptionUtil, confirmationTokenRepository, jwtTokenUtil, userRepository, userRoleRepository
        );
        assertNull(process.getToken(null));
    }

    @Test
    void testGetToken_SaveFailed() {
        UserRegistrationProcess process = new UserRegistrationProcess(
                emailService, encryptionUtil, confirmationTokenRepository, jwtTokenUtil, userRepository, userRoleRepository
        );
        UserEntity user = new UserEntity();
        user.setUserId(1L);
        user.setTokenVersion(2);

        ConfirmationTokenResult generatedToken = new ConfirmationTokenResult("rawToken", "jti", new Date(), new Date());

        when(jwtTokenUtil.generateConfirmationToken(1L, 2)).thenReturn(generatedToken);
        when(encryptionUtil.encrypt("rawToken")).thenReturn("encryptedToken");
        when(confirmationTokenRepository.save(eq(1L), eq("encryptedToken"), any(Timestamp.class), eq(2), eq("jti"), eq(false))).thenReturn(0);

        assertNull(process.getToken(user));
    }

    @Test
    void testSendEmail_UserRegistration() {
        UserRegistrationProcess process = new UserRegistrationProcess(
                emailService, encryptionUtil, confirmationTokenRepository, jwtTokenUtil, userRepository, userRoleRepository
        );
        UserEntity user = new UserEntity();
        user.setEmail("test@example.com");
        ConfirmationTokenEntity tokenEntity = new ConfirmationTokenEntity();

        AccountResponse response = process.sendEmail(user, tokenEntity);

        assertTrue(response.isSuccess());
        assertEquals(AccountResponseType.VERIFICATION_MAIL_FROM_REGISTRATION, response.getType());
        verify(emailService).sendEmail(eq("test@example.com"), anyString(), anyString());
    }

    @Test
    void testSendEmail_UnlockAccount() {
        UnlockAccountProcess process = new UnlockAccountProcess(
                emailService, encryptionUtil, confirmationTokenRepository, jwtTokenUtil, userRepository
        );
        UserEntity user = new UserEntity();
        user.setEmail("test@example.com");
        ConfirmationTokenEntity tokenEntity = new ConfirmationTokenEntity();

        AccountResponse response = process.sendEmail(user, tokenEntity);

        assertTrue(response.isSuccess());
        assertEquals(AccountResponseType.VERIFICATION_MAIL_FROM_UNLOCK, response.getType());
        verify(emailService).sendEmail(eq("test@example.com"), anyString(), anyString());
    }
}
