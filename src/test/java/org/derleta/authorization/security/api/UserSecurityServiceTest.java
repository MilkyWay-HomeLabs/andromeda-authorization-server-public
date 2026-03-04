package org.derleta.authorization.security.api;

import org.derleta.authorization.domain.entity.UserEntity;
import org.derleta.authorization.repository.impl.UserRepository;
import org.derleta.authorization.repository.impl.UserRolesRepository;
import org.derleta.authorization.security.model.UserSecurity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserSecurityServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserRolesRepository userRolesRepository;

    private UserSecurityService userSecurityService;

    @BeforeEach
    void setUp() {
        userSecurityService = new UserSecurityService(userRepository, userRolesRepository);
    }

    @Test
    void testLoadUserSecurity_Success() {
        Long userId = 1L;
        UserEntity userEntity = new UserEntity();
        userEntity.setUserId(userId);
        userEntity.setUsername("testuser");
        userEntity.setEmail("test@example.com");
        userEntity.setPassword("encodedPassword");
        userEntity.setVerified(true);
        userEntity.setBlocked(false);
        userEntity.setTokenVersion(1);

        when(userRepository.findById(userId)).thenReturn(userEntity);
        when(userRolesRepository.getRoles(userId)).thenReturn(Collections.emptyList());

        UserSecurity result = userSecurityService.loadUserSecurity(userId);

        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals("testuser", result.getUsername());
        verify(userRepository).findById(userId);
        verify(userRolesRepository).getRoles(userId);
    }

    @Test
    void testLoadUserSecurity_UserNotFound() {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                userSecurityService.loadUserSecurity(userId)
        );

        assertEquals("User not found", exception.getMessage());
        verify(userRepository).findById(userId);
        verifyNoInteractions(userRolesRepository);
    }
}
