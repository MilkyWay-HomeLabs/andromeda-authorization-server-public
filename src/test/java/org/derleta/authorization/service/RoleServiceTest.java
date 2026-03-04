package org.derleta.authorization.service;

import org.derleta.authorization.domain.entity.RoleEntity;
import org.derleta.authorization.domain.model.Role;
import org.derleta.authorization.repository.impl.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @Mock
    private RoleRepository repository;

    @InjectMocks
    private RoleService service;

    private RoleEntity roleEntity;
    private Role role;

    @BeforeEach
    void setUp() {
        roleEntity = new RoleEntity(1, "ROLE_USER");
        role = new Role(1, "ROLE_USER");
    }

    @Test
    void testGetList() {
        when(repository.findAll(anyString())).thenReturn(List.of(roleEntity));

        Set<Role> result = service.getList("USER");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("ROLE_USER", result.iterator().next().roleName());
        verify(repository).findAll("USER");
    }

    @Test
    void testGetPage() {
        when(repository.getSortedPageWithFilters(anyInt(), anyInt(), anyString(), anyString(), anyString()))
                .thenReturn(Set.of(roleEntity));
        when(repository.getFiltersCount(anyString())).thenReturn(1);

        Page<Role> result = service.getPage(0, 10, "roleName", "asc", "USER");

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        assertEquals("ROLE_USER", result.getContent().getFirst().roleName());
        verify(repository).getSortedPageWithFilters(0, 10, "role_name", "ASC", "USER");
    }

    @Test
    void testGetPage_DefaultSort() {
        when(repository.getSortedPageWithFilters(anyInt(), anyInt(), anyString(), anyString(), anyString()))
                .thenReturn(Set.of(roleEntity));
        when(repository.getFiltersCount(anyString())).thenReturn(1);

        service.getPage(0, 10, "invalid", "desc", "USER");

        verify(repository).getSortedPageWithFilters(0, 10, "role_id", "DESC", "USER");
    }

    @Test
    void testGet_Found() {
        when(repository.findById(1)).thenReturn(roleEntity);

        Role result = service.get(1);

        assertNotNull(result);
        assertEquals(1, result.roleId());
        assertEquals("ROLE_USER", result.roleName());
    }

    @Test
    void testGet_NotFound() {
        when(repository.findById(1)).thenReturn(null);

        Role result = service.get(1);

        assertNull(result);
    }

    @Test
    void testSave_Success() {
        when(repository.save(any(Role.class))).thenReturn(1);
        when(repository.findById(1)).thenReturn(roleEntity);

        Role result = service.save(role);

        assertNotNull(result);
        assertEquals(1, result.roleId());
        verify(repository).save(role);
    }

    @Test
    void testSave_Failure() {
        when(repository.save(any(Role.class))).thenReturn(0);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> service.save(role));
        assertEquals("Failed to save role", exception.getMessage());
    }

    @Test
    void testUpdate_Success() {
        when(repository.findById(1)).thenReturn(roleEntity);
        when(repository.update(eq(1), any(Role.class))).thenReturn(1);

        Role result = service.update(1, role);

        assertNotNull(result);
        verify(repository).update(1, role);
    }

    @Test
    void testUpdate_NotFound() {
        when(repository.findById(1)).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> service.update(1, role));
        assertEquals("Role not found: 1", exception.getMessage());
    }

    @Test
    void testUpdate_Failure() {
        when(repository.findById(1)).thenReturn(roleEntity);
        when(repository.update(eq(1), any(Role.class))).thenReturn(0);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> service.update(1, role));
        assertEquals("Failed to update role: 1", exception.getMessage());
    }

    @Test
    void testDelete_Success() {
        when(repository.findById(1)).thenReturn(roleEntity);
        when(repository.deleteById(1)).thenReturn(1);

        boolean result = service.delete(1);

        assertTrue(result);
        verify(repository).deleteById(1);
    }

    @Test
    void testDelete_NotFound() {
        when(repository.findById(1)).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> service.delete(1));
        assertEquals("Role not found: 1", exception.getMessage());
    }

    @Test
    void testDelete_Failure() {
        when(repository.findById(1)).thenReturn(roleEntity);
        when(repository.deleteById(1)).thenReturn(0);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> service.delete(1));
        assertEquals("Failed to delete role: 1", exception.getMessage());
    }
}
