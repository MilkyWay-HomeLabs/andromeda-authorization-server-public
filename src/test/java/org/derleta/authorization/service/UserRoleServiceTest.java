package org.derleta.authorization.service;

import org.derleta.authorization.domain.entity.UserRoleEntity;
import org.derleta.authorization.domain.model.UserRole;
import org.derleta.authorization.domain.entity.UserEntity;
import org.derleta.authorization.domain.entity.RoleEntity;
import org.derleta.authorization.repository.impl.UserRoleRepository;
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
class UserRoleServiceTest {

    @Mock
    private UserRoleRepository repository;

    @InjectMocks
    private UserRoleService service;

    private UserRoleEntity userRoleEntity;
    private UserEntity userEntity;
    private RoleEntity roleEntity;

    @BeforeEach
    void setUp() {
        userEntity = new UserEntity(1L, "user", "user@example.com", "pass", null, null, true, false, 1);
        roleEntity = new RoleEntity(1, "ROLE_USER");
        userRoleEntity = new UserRoleEntity(100L, userEntity, roleEntity);
    }

    @Test
    void testGetPage() {
        when(repository.getSortedPageWithFilters(anyInt(), anyInt(), anyString(), anyString(), any(), any(), any()))
                .thenReturn(List.of(userRoleEntity));
        when(repository.getFiltersCount(any(), any(), any())).thenReturn(1L);

        Page<UserRole> result = service.getPage(0, 10, "username", "asc", "user", null, null);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        assertEquals("user", result.getContent().getFirst().user().username());
        verify(repository).getSortedPageWithFilters(0, 10, "u.username", "ASC", "user", null, null);
    }

    @Test
    void testGetPage_SortByEmail() {
        when(repository.getSortedPageWithFilters(anyInt(), anyInt(), anyString(), anyString(), any(), any(), any()))
                .thenReturn(List.of(userRoleEntity));
        when(repository.getFiltersCount(any(), any(), any())).thenReturn(1L);

        service.getPage(0, 10, "email", "desc", null, "user@example.com", null);

        verify(repository).getSortedPageWithFilters(0, 10, "u.email", "DESC", null, "user@example.com", null);
    }

    @Test
    void testGetPage_SortByRoleName() {
        when(repository.getSortedPageWithFilters(anyInt(), anyInt(), anyString(), anyString(), any(), any(), any()))
                .thenReturn(List.of(userRoleEntity));
        when(repository.getFiltersCount(any(), any(), any())).thenReturn(1L);

        service.getPage(0, 10, "roleName", "asc", null, null, "USER");

        verify(repository).getSortedPageWithFilters(0, 10, "r.role_name", "ASC", null, null, "USER");
    }

    @Test
    void testGetPage_DefaultSort() {
        when(repository.getSortedPageWithFilters(anyInt(), anyInt(), anyString(), anyString(), any(), any(), any()))
                .thenReturn(List.of(userRoleEntity));
        when(repository.getFiltersCount(any(), any(), any())).thenReturn(1L);

        service.getPage(0, 10, "invalid", "asc", null, null, null);

        verify(repository).getSortedPageWithFilters(0, 10, "u.user_id", "ASC", null, null, null);
    }

    @Test
    void testGet_Found() {
        when(repository.findById(100L)).thenReturn(userRoleEntity);

        UserRole result = service.get(100L);

        assertNotNull(result);
        assertEquals(100L, result.userRoleId());
    }

    @Test
    void testGet_NotFound() {
        when(repository.findById(100L)).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> service.get(100L));
        assertTrue(exception.getMessage().contains("User\\-role association not found: 100"),
                "Expected message to contain 'User\\-role association not found: 100', but was: " + exception.getMessage());
    }

    @Test
    void testSave_Success() {
        when(repository.save(1L, 1)).thenReturn(100L);
        when(repository.findById(100L)).thenReturn(userRoleEntity);

        UserRole result = service.save(1L, 1);

        assertNotNull(result);
        assertEquals(100L, result.userRoleId());
        verify(repository).save(1L, 1);
    }

    @Test
    void testSave_Failure() {
        when(repository.save(1L, 1)).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> service.save(1L, 1));
        assertTrue(exception.getMessage().contains("Failed to save user\\-role association"),
                "Expected message to contain 'Failed to save user\\-role association', but was: " + exception.getMessage());
    }

    @Test
    void testDelete_Success() {
        when(repository.findByIds(1L, 1)).thenReturn(userRoleEntity);
        when(repository.deleteById(1L, 1)).thenReturn(1);

        boolean result = service.delete(1L, 1);

        assertTrue(result);
        verify(repository).deleteById(1L, 1);
    }

    @Test
    void testDelete_NotFound() {
        when(repository.findByIds(1L, 1)).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> service.delete(1L, 1));
        assertTrue(exception.getMessage().contains("User\\-role association not found"),
                "Expected message to contain 'User\\-role association not found', but was: " + exception.getMessage());
    }

    @Test
    void testDelete_Failure() {
        when(repository.findByIds(1L, 1)).thenReturn(userRoleEntity);
        when(repository.deleteById(1L, 1)).thenReturn(0);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> service.delete(1L, 1));
        assertTrue(exception.getMessage().contains("Failed to delete user\\-role association"),
                "Expected message to contain 'Failed to delete user\\-role association', but was: " + exception.getMessage());
    }
}
