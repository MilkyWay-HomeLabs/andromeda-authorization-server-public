package org.derleta.authorization.service;

import org.derleta.authorization.domain.entity.UserRoleEntity;
import org.derleta.authorization.domain.model.UserRoles;
import org.derleta.authorization.domain.entity.UserEntity;
import org.derleta.authorization.domain.entity.RoleEntity;
import org.derleta.authorization.repository.impl.UserRolesRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserRolesServiceTest {

    @Mock
    private UserRolesRepository repository;

    @InjectMocks
    private UserRolesService service;

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
    void testGet_Success() {
        when(repository.get(anyLong(), any(), any(), any()))
                .thenReturn(List.of(userRoleEntity));

        UserRoles result = service.get(1L, "roleName", "desc", "USER");

        assertNotNull(result);
        assertEquals(1, result.roles().size());
        verify(repository).get(1L, "r.role_name", "DESC", "USER");
    }

    @Test
    void testGet_DefaultSort() {
        when(repository.get(anyLong(), any(), any(), any()))
                .thenReturn(List.of(userRoleEntity));

        service.get(1L, "invalid", "asc", null);

        verify(repository).get(1L, "r.role_id", "ASC", null);
    }
}
