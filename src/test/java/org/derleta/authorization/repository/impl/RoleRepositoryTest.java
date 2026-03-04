package org.derleta.authorization.repository.impl;

import org.derleta.authorization.domain.entity.RoleEntity;
import org.derleta.authorization.domain.model.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.test.util.ReflectionTestUtils;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

class RoleRepositoryTest {

    @Mock
    private DataSource dataSource;

    @Mock
    private JdbcTemplate jdbcTemplate;

    private RoleRepository repository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        repository = new RoleRepository(dataSource);
        ReflectionTestUtils.setField(repository, "jdbcTemplate", jdbcTemplate);
    }

    @Test
    void testGetSize() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class))).thenReturn(5);
        assertEquals(5, repository.getSize());
    }

    @Test
    void testGetPage() {
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), anyInt(), anyInt()))
                .thenReturn(Collections.emptyList());
        assertNotNull(repository.getPage(0, 10));
    }

    @Test
    void testFindAll() {
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), anyString()))
                .thenReturn(Collections.emptyList());
        assertNotNull(repository.findAll("ROLE_USER"));
    }

    @Test
    void testFindById() {
        RoleEntity role = new RoleEntity();
        when(jdbcTemplate.queryForObject(anyString(), any(RowMapper.class), anyInt()))
                .thenReturn(role);
        assertEquals(role, repository.findById(1));
    }

    @Test
    void testSave() {
        Role role = new Role(1, "ROLE_ADMIN");
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), anyString())).thenReturn(1);
        assertEquals(1, repository.save(role));
    }

    @Test
    void testUpdate() {
        Role role = new Role(1, "ROLE_ADMIN");
        when(jdbcTemplate.update(anyString(), anyString(), anyInt())).thenReturn(1);
        assertEquals(1, repository.update(1, role));
    }

    @Test
    void testDeleteById() {
        when(jdbcTemplate.update(anyString(), anyInt())).thenReturn(1);
        assertEquals(1, repository.deleteById(1));
    }

    @Test
    void testGetSortedPageWithFilters() {
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), anyString(), anyInt(), anyInt()))
                .thenReturn(Collections.emptyList());
        Set<RoleEntity> result = repository.getSortedPageWithFilters(0, 10, "role_name", "ASC", "ADMIN");
        assertNotNull(result);
    }

    @Test
    void testGetFiltersCount() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), anyString())).thenReturn(2);
        assertEquals(2, repository.getFiltersCount("ADMIN"));
    }
}
