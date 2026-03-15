package org.derleta.authorization.service;

import org.derleta.authorization.domain.entity.UserEntity;
import org.derleta.authorization.domain.model.User;
import org.derleta.authorization.repository.impl.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository repository;

    @InjectMocks
    private UserService service;

    private UserEntity userEntity;
    private User user;

    @BeforeEach
    void setUp() {
        userEntity = new UserEntity(1L, "user", "user@example.com", "pass", null, null, true, false, 1);
        user = new User(1L, "user", "user@example.com", "pass", null, null, true, false, 1);
    }

    @Test
    void testGetPage() {
        when(repository.getSortedPageWithFilters(anyInt(), anyInt(), anyString(), anyString(), any(), any()))
                .thenReturn(List.of(userEntity));
        when(repository.getFiltersCount(any(), any())).thenReturn(1L);

        Page<User> result = service.getPage(0, 10, "username", "asc", "user", null);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        assertEquals("user", result.getContent().getFirst().username());
        verify(repository).getSortedPageWithFilters(0, 10, "username", "ASC", "user", null);
    }

    @Test
    void testGetPage_SortByEmail() {
        when(repository.getSortedPageWithFilters(anyInt(), anyInt(), anyString(), anyString(), any(), any()))
                .thenReturn(List.of(userEntity));
        when(repository.getFiltersCount(any(), any())).thenReturn(1L);

        service.getPage(0, 10, "email", "desc", null, "user@example.com");

        verify(repository).getSortedPageWithFilters(0, 10, "email", "DESC", null, "user@example.com");
    }

    @Test
    void testGetPage_DefaultSort() {
        when(repository.getSortedPageWithFilters(anyInt(), anyInt(), anyString(), anyString(), any(), any()))
                .thenReturn(List.of(userEntity));
        when(repository.getFiltersCount(any(), any())).thenReturn(1L);

        service.getPage(0, 10, "invalid", "asc", null, null);

        verify(repository).getSortedPageWithFilters(0, 10, "user_id", "ASC", null, null);
    }

    @Test
    void testGet_Found() {
        when(repository.findById(1L)).thenReturn(userEntity);

        User result = service.get(1L);

        assertNotNull(result);
        assertEquals(1L, result.userId());
        assertEquals("user", result.username());
    }

    @Test
    void testGet_NotFound() {
        when(repository.findById(1L)).thenReturn(null);

        User result = service.get(1L);

        assertNull(result);
    }

    @Test
    void testGet_InvalidId() {
        UserEntity invalidEntity = new UserEntity(0L, "user", "user@example.com", "pass", null, null, true, false, 1);
        when(repository.findById(1L)).thenReturn(invalidEntity);

        User result = service.get(1L);

        assertNull(result);
    }

    @Test
    void testSave_Success() {
        when(repository.save(any(UserEntity.class))).thenReturn(1L);
        when(repository.findById(1L)).thenReturn(userEntity);

        User result = service.save(user);

        assertNotNull(result);
        assertEquals(1L, result.userId());
        verify(repository).save(any(UserEntity.class));
    }

    @Test
    void testSave_Failure() {
        when(repository.save(any(UserEntity.class))).thenReturn(0L);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> service.save(user));
        assertEquals("Failed to save user", exception.getMessage());
    }

    @Test
    void testUpdate_Success() {
        when(repository.findById(1L)).thenReturn(userEntity);
        // repository.update is void
        when(repository.findById(1L)).thenReturn(userEntity); // second call for get(userId)

        User result = service.update(1L, user);

        assertNotNull(result);
        verify(repository).update(eq(1L), any(UserEntity.class));
    }

    @Test
    void testUpdate_NotFound() {
        when(repository.findById(1L)).thenReturn(null);

        User result = service.update(1L, user);

        assertNull(result);
        verify(repository, never()).update(anyLong(), any(UserEntity.class));
    }

    @Test
    void testDelete_Success() {
        when(repository.deleteById(1L)).thenReturn(1);

        boolean result = service.delete(1L);

        assertTrue(result);
        verify(repository).deleteById(1L);
    }

    @Test
    void testDelete_Failure() {
        when(repository.deleteById(1L)).thenReturn(0);

        boolean result = service.delete(1L);

        assertFalse(result);
    }
}
