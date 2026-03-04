package org.derleta.authorization.service.accounts.process;

import org.derleta.authorization.controller.dto.request.Request;
import org.derleta.authorization.controller.dto.request.impl.UserConfirmationRequest;
import org.derleta.authorization.controller.dto.response.AccountResponse;
import org.derleta.authorization.domain.entity.token.ConfirmationTokenEntity;
import org.derleta.authorization.domain.types.AccountResponseType;
import org.derleta.authorization.repository.RepositoryClass;
import org.derleta.authorization.repository.impl.UserRepository;
import org.derleta.authorization.repository.impl.token.ConfirmationTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConfirmationTokenProcessTest {

    @Mock
    private ConfirmationTokenRepository confirmationTokenRepository;

    @Mock
    private UserRepository userRepository;

    private ConfirmationTokenProcess confirmationTokenProcess;

    @BeforeEach
    void setUp() {
        Set<RepositoryClass> repositories = new HashSet<>();
        repositories.add(userRepository);
        repositories.add(confirmationTokenRepository);
        confirmationTokenProcess = new ConfirmationTokenProcess(repositories);
    }

    @Test
    void testCheck_Success() {
        UserConfirmationRequest request = new UserConfirmationRequest(1L, "validToken");
        ConfirmationTokenEntity tokenEntity = new ConfirmationTokenEntity();
        tokenEntity.setToken("validToken");
        tokenEntity.setExpirationDate(Timestamp.from(Instant.now().plus(1, ChronoUnit.HOURS)));

        when(confirmationTokenRepository.findById(1L)).thenReturn(Optional.of(tokenEntity));

        AccountResponse response = confirmationTokenProcess.check(request);

        assertTrue(response.isSuccess());
        assertEquals(AccountResponseType.TOKEN_IS_VALID, response.getType());
    }

    @Test
    void testCheck_WrongRequestType() {
        Request request = mock(Request.class);
        AccountResponse response = confirmationTokenProcess.check(request);

        assertFalse(response.isSuccess());
        assertEquals(AccountResponseType.BAD_CONFIRMATION_REQUEST_TYPE, response.getType());
    }

    @Test
    void testCheck_TokenNotFound() {
        UserConfirmationRequest request = new UserConfirmationRequest(1L, "someToken");
        when(confirmationTokenRepository.findById(1L)).thenReturn(Optional.empty());

        AccountResponse response = confirmationTokenProcess.check(request);

        assertFalse(response.isSuccess());
        assertEquals(AccountResponseType.TOKEN_NOT_FOUND, response.getType());
    }

    @Test
    void testCheck_InvalidTokenValue() {
        UserConfirmationRequest request = new UserConfirmationRequest(1L, "wrongToken");
        ConfirmationTokenEntity tokenEntity = new ConfirmationTokenEntity();
        tokenEntity.setToken("correctToken");

        when(confirmationTokenRepository.findById(1L)).thenReturn(Optional.of(tokenEntity));

        AccountResponse response = confirmationTokenProcess.check(request);

        assertFalse(response.isSuccess());
        assertEquals(AccountResponseType.INVALID_TOKEN_VALUE, response.getType());
    }

    @Test
    void testCheck_TokenExpired() {
        UserConfirmationRequest request = new UserConfirmationRequest(1L, "someToken");
        ConfirmationTokenEntity tokenEntity = new ConfirmationTokenEntity();
        tokenEntity.setToken("someToken");
        tokenEntity.setExpirationDate(Timestamp.from(Instant.now().minus(1, ChronoUnit.HOURS)));

        when(confirmationTokenRepository.findById(1L)).thenReturn(Optional.of(tokenEntity));

        AccountResponse response = confirmationTokenProcess.check(request);

        assertFalse(response.isSuccess());
        assertEquals(AccountResponseType.TOKEN_EXPIRED, response.getType());
    }

    @Test
    void testUpdate_Success() {
        UserConfirmationRequest request = new UserConfirmationRequest(1L, "someToken");
        ConfirmationTokenEntity tokenEntity = new ConfirmationTokenEntity();
        tokenEntity.setTokenId(1L);
        tokenEntity.setUserId(10L);

        when(confirmationTokenRepository.findById(1L)).thenReturn(Optional.of(tokenEntity));
        when(confirmationTokenRepository.setExpired(1L)).thenReturn(1);

        AccountResponse response = confirmationTokenProcess.update(request);

        assertTrue(response.isSuccess());
        assertEquals(AccountResponseType.ACCOUNT_CONFIRMED, response.getType());
        verify(userRepository, times(1)).unlock(10L);
    }

    @Test
    void testUpdate_TokenNotFound() {
        UserConfirmationRequest request = new UserConfirmationRequest(1L, "someToken");
        when(confirmationTokenRepository.findById(1L)).thenReturn(Optional.empty());

        AccountResponse response = confirmationTokenProcess.update(request);

        assertFalse(response.isSuccess());
        assertEquals(AccountResponseType.TOKEN_NOT_FOUND, response.getType());
    }
}
