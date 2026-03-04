package org.derleta.authorization.repository.impl;

import org.derleta.authorization.domain.entity.RoleEntity;
import org.derleta.authorization.domain.entity.UserEntity;
import org.derleta.authorization.domain.entity.UserRoleEntity;
import org.derleta.authorization.security.model.UserSecurity;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class TokensGeneratorRepositoryTest {

    @Mock
    private DataSource dataSource;

    @Mock
    private JdbcTemplate jdbcTemplate;

    private TokensGeneratorRepository repository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        repository = new TokensGeneratorRepository(dataSource);
        ReflectionTestUtils.setField(repository, "jdbcTemplate", jdbcTemplate);
    }

    @Test
    void testFindByLogin() {
        UserEntity user = new UserEntity();
        user.setUserId(1L);
        RoleEntity role = new RoleEntity(1, "ROLE_USER");
        UserRoleEntity userRole = new UserRoleEntity(1L, user, role);

        when(jdbcTemplate.query(anyString(), any(RowMapper.class), anyString()))
                .thenReturn(List.of(userRole));

        Optional<UserSecurity> result = repository.findByLogin("user");
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
    }

    @Test
    void testFindByEmail() {
        UserEntity user = new UserEntity();
        user.setUserId(1L);
        RoleEntity role = new RoleEntity(1, "ROLE_USER");
        UserRoleEntity userRole = new UserRoleEntity(1L, user, role);

        when(jdbcTemplate.query(anyString(), any(RowMapper.class), anyString()))
                .thenReturn(List.of(userRole));

        Optional<UserSecurity> result = repository.findByEmail("test@example.com");
        assertTrue(result.isPresent());
    }

    @Test
    void testFindByLogin_Empty() {
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), anyString()))
                .thenReturn(Collections.emptyList());

        Optional<UserSecurity> result = repository.findByLogin("unknown");
        assertFalse(result.isPresent());
    }
}
