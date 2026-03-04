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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UnlockAccountProcessTest {

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

    private UnlockAccountProcess unlockAccountProcess;

    @BeforeEach
    void setUp() {
        unlockAccountProcess = new UnlockAccountProcess(
                emailService,
                encryptionUtil,
                confirmationTokenRepository,
                jwtTokenUtil,
                userRepository
        );
    }

    @Test
    void testCheck_Success() {
        UserUnlockRequest request = new UserUnlockRequest(1L);
        UserEntity user = new UserEntity();
        user.setUserId(1L);

        when(userRepository.findById(1L)).thenReturn(user);
        when(userRepository.isVerified(1L)).thenReturn(false);
        when(userRepository.isBlocked(1L)).thenReturn(true);

        AccountResponse response = unlockAccountProcess.check(request);

        assertTrue(response.isSuccess());
        assertEquals(AccountResponseType.ACCOUNT_CAN_BE_UNLOCKED, response.getType());
    }

    @Test
    void testCheck_WrongRequestType() {
        Request request = mock(Request.class);
        AccountResponse response = unlockAccountProcess.check(request);

        assertFalse(response.isSuccess());
        assertEquals(AccountResponseType.BAD_UNLOCK_REQUEST_TYPE, response.getType());
    }

    @Test
    void testCheck_AccountNotFound() {
        UserUnlockRequest request = new UserUnlockRequest(1L);
        when(userRepository.findById(1L)).thenReturn(null);

        AccountResponse response = unlockAccountProcess.check(request);

        assertFalse(response.isSuccess());
        assertEquals(AccountResponseType.ACCOUNT_NOT_EXIST_UNLOCK_ACCOUNT, response.getType());
    }

    @Test
    void testCheck_AccountAlreadyUnlocked() {
        UserUnlockRequest request = new UserUnlockRequest(1L);
        UserEntity user = new UserEntity();
        user.setUserId(1L);

        when(userRepository.findById(1L)).thenReturn(user);
        when(userRepository.isVerified(1L)).thenReturn(true);
        when(userRepository.isBlocked(1L)).thenReturn(false);

        AccountResponse response = unlockAccountProcess.check(request);

        assertFalse(response.isSuccess());
        assertEquals(AccountResponseType.ACCOUNT_VERIFIED_AND_NOT_BLOCKED, response.getType());
    }

    @Test
    void testSave() {
        UserUnlockRequest request = new UserUnlockRequest(1L);
        UserEntity user = new UserEntity();
        user.setUserId(1L);

        when(userRepository.findById(1L)).thenReturn(user);

        UserEntity result = unlockAccountProcess.save(request);

        assertNotNull(result);
        assertEquals(1L, result.getUserId());
        verify(userRepository, times(1)).updateStatus(1L, false, false);
    }

    @Test
    void testSave_WrongRequestType() {
        Request request = mock(Request.class);
        UserEntity result = unlockAccountProcess.save(request);

        assertNull(result);
    }
}
