package org.derleta.authorization.service.accounts.process;

import org.derleta.authorization.controller.dto.request.Request;
import org.derleta.authorization.controller.dto.request.impl.UserRegistrationRequest;
import org.derleta.authorization.controller.dto.response.AccountResponse;
import org.derleta.authorization.domain.entity.UserEntity;
import org.derleta.authorization.domain.types.AccountResponseType;
import org.derleta.authorization.mail.EmailService;
import org.derleta.authorization.repository.impl.UserRepository;
import org.derleta.authorization.repository.impl.UserRoleRepository;
import org.derleta.authorization.repository.impl.token.ConfirmationTokenRepository;
import org.derleta.authorization.security.util.EncryptionUtil;
import org.derleta.authorization.security.util.JwtTokenUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserRegistrationProcessTest {

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

    private UserRegistrationProcess userRegistrationProcess;

    @BeforeEach
    void setUp() {
        userRegistrationProcess = new UserRegistrationProcess(
                emailService,
                encryptionUtil,
                confirmationTokenRepository,
                jwtTokenUtil,
                userRepository,
                userRoleRepository
        );
    }

    @Test
    void testCheck_Success() {
        UserRegistrationRequest request = new UserRegistrationRequest("user", "password", "test@example.com");
        when(userRepository.isEmailExist("test@example.com")).thenReturn(false);
        when(userRepository.isLoginExist("user")).thenReturn(false);

        AccountResponse response = userRegistrationProcess.check(request);

        assertTrue(response.isSuccess());
        assertEquals(AccountResponseType.UNIQUE_LOGIN_AND_EMAIL, response.getType());
    }

    @Test
    void testCheck_WrongRequestType() {
        Request request = mock(Request.class);
        AccountResponse response = userRegistrationProcess.check(request);

        assertFalse(response.isSuccess());
        assertEquals(AccountResponseType.BAD_REGISTRATION_REQUEST_TYPE, response.getType());
    }

    @Test
    void testCheck_EmailNotUnique() {
        UserRegistrationRequest request = new UserRegistrationRequest("user", "password", "test@example.com");
        when(userRepository.isEmailExist("test@example.com")).thenReturn(true);

        AccountResponse response = userRegistrationProcess.check(request);

        assertFalse(response.isSuccess());
        assertEquals(AccountResponseType.EMAIL_IS_NOT_UNIQUE, response.getType());
    }

    @Test
    void testCheck_LoginNotUnique() {
        UserRegistrationRequest request = new UserRegistrationRequest("user", "password", "test@example.com");
        when(userRepository.isEmailExist("test@example.com")).thenReturn(false);
        when(userRepository.isLoginExist("user")).thenReturn(true);

        AccountResponse response = userRegistrationProcess.check(request);

        assertFalse(response.isSuccess());
        assertEquals(AccountResponseType.LOGIN_IS_NOT_UNIQUE, response.getType());
    }

    @Test
    void testSave_Success() {
        UserRegistrationRequest request = new UserRegistrationRequest("user", "password", "test@example.com");
        UserEntity user = new UserEntity();
        user.setUserId(1L);

        when(userRepository.save(any(UserEntity.class))).thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(user);

        UserEntity result = userRegistrationProcess.save(request);

        assertNotNull(result);
        assertEquals(1L, result.getUserId());
        verify(userRoleRepository, times(1)).save(1L, 1);
    }

    @Test
    void testSave_SaveFailed() {
        UserRegistrationRequest request = new UserRegistrationRequest("user", "password", "test@example.com");
        when(userRepository.save(any(UserEntity.class))).thenReturn(null);

        assertThrows(RuntimeException.class, () -> userRegistrationProcess.save(request));
    }

    @Test
    void testSave_WrongRequestType() {
        Request request = mock(Request.class);
        assertThrows(RuntimeException.class, () -> userRegistrationProcess.save(request));
    }
}
