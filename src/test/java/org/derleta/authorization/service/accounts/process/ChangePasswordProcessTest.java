package org.derleta.authorization.service.accounts.process;

import org.derleta.authorization.controller.dto.request.Request;
import org.derleta.authorization.controller.dto.request.impl.ChangePasswordRequest;
import org.derleta.authorization.controller.dto.response.AccountResponse;
import org.derleta.authorization.domain.entity.UserEntity;
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
class ChangePasswordProcessTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private PasswordEncoder encoder;

    private ChangePasswordProcess changePasswordProcess;

    @BeforeEach
    void setUp() {
        Set<RepositoryClass> repositories = new HashSet<>();
        repositories.add(userRepository);
        changePasswordProcess = new ChangePasswordProcess(repositories, emailService, encoder);
    }

    @Test
    void testCheck_Success() {
        ChangePasswordRequest request = new ChangePasswordRequest(1L, "test@example.com", "oldPass", "newPass");
        UserEntity user = new UserEntity();
        user.setUserId(1L);
        user.setEmail("test@example.com");
        user.setPassword("encodedOldPass");

        when(userRepository.findByEmail("test@example.com")).thenReturn(user);
        when(userRepository.isBlocked(1L)).thenReturn(false);
        when(encoder.matches("oldPass", "encodedOldPass")).thenReturn(true);

        AccountResponse response = changePasswordProcess.check(request);

        assertTrue(response.isSuccess());
        assertEquals(AccountResponseType.PASSWORD_CAN_BE_CHANGED, response.getType());
    }

    @Test
    void testCheck_WrongRequestType() {
        Request request = mock(Request.class);
        AccountResponse response = changePasswordProcess.check(request);

        assertFalse(response.isSuccess());
        assertEquals(AccountResponseType.BAD_RESET_PASSWD_REQUEST_TYPE, response.getType());
    }

    @Test
    void testCheck_EmailNotExist() {
        ChangePasswordRequest request = new ChangePasswordRequest(1L, "nonexistent@example.com", "oldPass", "newPass");
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(null);

        AccountResponse response = changePasswordProcess.check(request);

        assertFalse(response.isSuccess());
        assertEquals(AccountResponseType.EMAIL_NOT_EXIST_CHANGE_PASSWD, response.getType());
    }

    @Test
    void testCheck_AccountBlocked() {
        ChangePasswordRequest request = new ChangePasswordRequest(1L, "test@example.com", "oldPass", "newPass");
        UserEntity user = new UserEntity();
        user.setUserId(1L);
        user.setEmail("test@example.com");

        when(userRepository.findByEmail("test@example.com")).thenReturn(user);
        when(userRepository.isBlocked(1L)).thenReturn(true);

        AccountResponse response = changePasswordProcess.check(request);

        assertFalse(response.isSuccess());
        assertEquals(AccountResponseType.ACCOUNT_IS_BLOCKED_CHANGE_PASSWD, response.getType());
    }

    @Test
    void testCheck_WrongActualPassword() {
        ChangePasswordRequest request = new ChangePasswordRequest(1L, "test@example.com", "wrongPass", "newPass");
        UserEntity user = new UserEntity();
        user.setUserId(1L);
        user.setEmail("test@example.com");
        user.setPassword("encodedOldPass");

        when(userRepository.findByEmail("test@example.com")).thenReturn(user);
        when(userRepository.isBlocked(1L)).thenReturn(false);
        when(encoder.matches("wrongPass", "encodedOldPass")).thenReturn(false);

        AccountResponse response = changePasswordProcess.check(request);

        assertFalse(response.isSuccess());
        assertEquals(AccountResponseType.BAD_ACTUAL_PASSWORD_CHANGE_PASSWD, response.getType());
    }

    @Test
    void testUpdate_Success() {
        ChangePasswordRequest request = new ChangePasswordRequest(1L, "test@example.com", "oldPass", "newPass");
        UserEntity user = new UserEntity();
        user.setUserId(1L);
        user.setEmail("test@example.com");

        when(userRepository.findByEmail("test@example.com")).thenReturn(user);
        when(encoder.encode("newPass")).thenReturn("encodedNewPass");
        when(userRepository.updatePassword(1L, "encodedNewPass")).thenReturn(1);

        AccountResponse response = changePasswordProcess.update(request);

        assertTrue(response.isSuccess());
        assertEquals(AccountResponseType.PASSWORD_CHANGED, response.getType());
    }

    @Test
    void testUpdate_WrongRequestType() {
        Request request = mock(Request.class);
        AccountResponse response = changePasswordProcess.update(request);

        assertFalse(response.isSuccess());
        assertEquals(AccountResponseType.PASSWORD_NOT_CHANGED, response.getType());
    }

    @Test
    void testUpdate_UserNotFound() {
        ChangePasswordRequest request = new ChangePasswordRequest(1L, "test@example.com", "oldPass", "newPass");
        when(userRepository.findByEmail("test@example.com")).thenReturn(null);

        AccountResponse response = changePasswordProcess.update(request);

        assertFalse(response.isSuccess());
        assertEquals(AccountResponseType.EMAIL_NOT_EXIST_CHANGE_PASSWD, response.getType());
    }

    @Test
    void testUpdate_UpdateFailed() {
        ChangePasswordRequest request = new ChangePasswordRequest(1L, "test@example.com", "oldPass", "newPass");
        UserEntity user = new UserEntity();
        user.setUserId(1L);
        user.setEmail("test@example.com");

        when(userRepository.findByEmail("test@example.com")).thenReturn(user);
        when(encoder.encode("newPass")).thenReturn("encodedNewPass");
        when(userRepository.updatePassword(1L, "encodedNewPass")).thenReturn(0);

        AccountResponse response = changePasswordProcess.update(request);

        assertFalse(response.isSuccess());
        assertEquals(AccountResponseType.PASSWORD_NOT_CHANGED, response.getType());
    }

    @Test
    void testSendMail() {
        String email = "test@example.com";
        AccountResponse response = changePasswordProcess.sendMail(email);

        assertTrue(response.isSuccess());
        assertEquals(AccountResponseType.MAIL_NEW_PASSWD_SENT, response.getType());
        verify(emailService, times(1)).sendEmail(eq(email), anyString(), anyString());
    }
}
