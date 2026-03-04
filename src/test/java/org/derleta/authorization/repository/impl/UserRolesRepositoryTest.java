package org.derleta.authorization.repository.impl;

import org.derleta.authorization.domain.entity.RoleEntity;
import org.derleta.authorization.domain.entity.UserRoleEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.test.util.ReflectionTestUtils;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

class UserRolesRepositoryTest {

    @Mock
    private DataSource dataSource;

    @Mock
    private JdbcTemplate jdbcTemplate;

    private UserRolesRepository repository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        repository = new UserRolesRepository(dataSource);
        ReflectionTestUtils.setField(repository, "jdbcTemplate", jdbcTemplate);
    }

    @Test
    void testGetWithFilters() {
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), anyLong(), anyString()))
                .thenReturn(Collections.emptyList());
        List<UserRoleEntity> result = repository.get(1L, "u.username", "ASC", "ROLE");
        assertNotNull(result);
    }

    @Test
    void testGetWithUsernameEmail() {
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), anyString(), anyString()))
                .thenReturn(Collections.emptyList());
        List<UserRoleEntity> result = repository.get("user", "email");
        assertNotNull(result);
    }

    @Test
    void testGetRoles() {
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), anyLong()))
                .thenReturn(Collections.emptyList());
        List<RoleEntity> result = repository.getRoles(1L);
        assertNotNull(result);
    }
}
