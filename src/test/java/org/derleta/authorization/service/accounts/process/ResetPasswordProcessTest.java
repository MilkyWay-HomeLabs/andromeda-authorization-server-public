package org.derleta.authorization.service.accounts.process;

import org.derleta.authorization.controller.dto.request.Request;
import org.derleta.authorization.controller.dto.request.impl.ResetPasswordRequest;
import org.derleta.authorization.controller.dto.response.AccountResponse;
import org.derleta.authorization.domain.entity.UserEntity;
import org.derleta.authorization.domain.entity.UserEntityDecrypted;
import org.derleta.authorization.domain.types.AccountResponseType;
import org.derleta.authorization.mail.EmailService;
import org.derleta.authorization.repository.RepositoryClass;
import org.derleta.authorization.repository.impl.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResetPasswordProcessTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private PasswordEncoder passwordEncoder;

    private ResetPasswordProcess resetPasswordProcess;

    @BeforeEach
    void setUp() {
        Set<RepositoryClass> repositories = new HashSet<>();
        repositories.add(userRepository);
        resetPasswordProcess = new ResetPasswordProcess(repositories, emailService, passwordEncoder);
    }

    @Test
    void testCheck_Success() {
        ResetPasswordRequest request = new ResetPasswordRequest("test@example.com");
        UserEntity user = new UserEntity();
        user.setUserId(1L);
        user.setEmail("test@example.com");

        when(userRepository.findByEmail("test@example.com")).thenReturn(user);
        when(userRepository.isBlocked(1L)).thenReturn(false);
        when(userRepository.isVerified(1L)).thenReturn(true);

        AccountResponse response = resetPasswordProcess.check(request);

        assertTrue(response.isSuccess());
        assertEquals(AccountResponseType.PASSWORD_CAN_BE_GENERATED, response.getType());
    }

    @Test
    void testCheck_WrongRequestType() {
        Request request = mock(Request.class);
        AccountResponse response = resetPasswordProcess.check(request);

        assertFalse(response.isSuccess());
        assertEquals(AccountResponseType.BAD_RESET_PASSWD_REQUEST_TYPE, response.getType());
    }

    @Test
    void testCheck_UserNotFound() {
        ResetPasswordRequest request = new ResetPasswordRequest("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(null);

        AccountResponse response = resetPasswordProcess.check(request);

        assertFalse(response.isSuccess());
        assertEquals(AccountResponseType.ACCOUNT_NOT_EXIST_RESET_PASSWD, response.getType());
    }

    @Test
    void testCheck_UserBlocked() {
        ResetPasswordRequest request = new ResetPasswordRequest("test@example.com");
        UserEntity user = new UserEntity();
        user.setUserId(1L);
        user.setEmail("test@example.com");

        when(userRepository.findByEmail("test@example.com")).thenReturn(user);
        when(userRepository.isBlocked(1L)).thenReturn(true);

        AccountResponse response = resetPasswordProcess.check(request);

        assertFalse(response.isSuccess());
        assertEquals(AccountResponseType.ACCOUNT_IS_BLOCKED_RESET_PASSWD, response.getType());
    }

    @Test
    void testCheck_UserNotVerified() {
        ResetPasswordRequest request = new ResetPasswordRequest("test@example.com");
        UserEntity user = new UserEntity();
        user.setUserId(1L);
        user.setEmail("test@example.com");

        when(userRepository.findByEmail("test@example.com")).thenReturn(user);
        when(userRepository.isBlocked(1L)).thenReturn(false);
        when(userRepository.isVerified(1L)).thenReturn(false);

        AccountResponse response = resetPasswordProcess.check(request);

        assertFalse(response.isSuccess());
        assertEquals(AccountResponseType.ACCOUNT_IS_NOT_VERIFIED, response.getType());
    }

    @Test
    void testSave_Success() {
        ResetPasswordRequest request = new ResetPasswordRequest("test@example.com");
        UserEntity user = new UserEntity();
        user.setUserId(1L);
        user.setEmail("test@example.com");

        when(userRepository.findByEmail("test@example.com")).thenReturn(user);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPass");
        when(userRepository.updatePassword(eq(1L), anyString())).thenReturn(1);
        when(userRepository.findById(1L)).thenReturn(user);

        UserEntity result = resetPasswordProcess.save(request);

        assertNotNull(result);
        assertInstanceOf(UserEntityDecrypted.class, result);
        assertEquals(1L, result.getUserId());
    }

    @Test
    void testSave_UserNotFound() {
        ResetPasswordRequest request = new ResetPasswordRequest("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(null);

        UserEntity result = resetPasswordProcess.save(request);

        assertNull(result);
    }

    @Test
    void testSave_UpdateFailed() {
        ResetPasswordRequest request = new ResetPasswordRequest("test@example.com");
        UserEntity user = new UserEntity();
        user.setUserId(1L);
        user.setEmail("test@example.com");

        when(userRepository.findByEmail("test@example.com")).thenReturn(user);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPass");
        when(userRepository.updatePassword(eq(1L), anyString())).thenReturn(0);

        UserEntity result = resetPasswordProcess.save(request);

        assertNull(result);
    }

    @Test
    void testSendMail() {
        UserEntity user = new UserEntity();
        user.setUserId(1L);
        user.setEmail("test@example.com");
        UserEntityDecrypted decrypted = new UserEntityDecrypted(user, "plainPassword");

        when(userRepository.findById(1L)).thenReturn(user);

        AccountResponse response = resetPasswordProcess.sendMail(decrypted);

        assertTrue(response.isSuccess());
        assertEquals(AccountResponseType.MAIL_NEW_PASSWD_SENT, response.getType());
        verify(emailService, times(1)).sendEmail(eq("test@example.com"), anyString(), anyString());
    }
}
