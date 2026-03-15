package org.derleta.authorization.repository.impl;

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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class UserRoleRepositoryTest {

    @Mock
    private DataSource dataSource;

    @Mock
    private JdbcTemplate jdbcTemplate;

    private UserRoleRepository repository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        repository = new UserRoleRepository(dataSource);
        ReflectionTestUtils.setField(repository, "jdbcTemplate", jdbcTemplate);
    }

    @Test
    void testGetSize() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class))).thenReturn(15);
        assertEquals(15, repository.getSize());
    }

    @Test
    void testGetPage() {
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), anyInt(), anyInt()))
                .thenReturn(Collections.emptyList());
        assertNotNull(repository.getPage(0, 10));
    }

    @Test
    void testFindByIds() {
        UserRoleEntity entity = new UserRoleEntity();
        when(jdbcTemplate.queryForObject(anyString(), any(RowMapper.class), anyLong(), anyInt()))
                .thenReturn(entity);
        assertEquals(entity, repository.findByIds(1L, 1));
    }

    @Test
    void testFindById() {
        UserRoleEntity entity = new UserRoleEntity();
        when(jdbcTemplate.queryForObject(anyString(), any(RowMapper.class), anyLong()))
                .thenReturn(entity);
        assertEquals(entity, repository.findById(1L));
    }


    @Test
    void testSave() {
        when(jdbcTemplate.update(any(org.springframework.jdbc.core.PreparedStatementCreator.class), any(org.springframework.jdbc.support.KeyHolder.class)))
                .thenAnswer(invocation -> {
                    org.springframework.jdbc.support.KeyHolder kh = invocation.getArgument(1);
                    ReflectionTestUtils.setField(kh, "keyList", List.of(java.util.Map.of("user_role_id", 123L)));
                    return 1;
                });

        Long result = repository.save(1L, 1);

        assertEquals(123L, result);

        verify(jdbcTemplate, times(1)).update(any(org.springframework.jdbc.core.PreparedStatementCreator.class), any(org.springframework.jdbc.support.KeyHolder.class));
        verifyNoMoreInteractions(jdbcTemplate);
    }

    @Test
    void testDeleteById() {
        when(jdbcTemplate.update(anyString(), anyLong(), anyInt())).thenReturn(1);
        assertEquals(1, repository.deleteById(1L, 1));
    }

    @Test
    void testGetSortedPageWithFilters() {
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), anyString(), anyString(), anyString(), anyInt(), anyInt()))
                .thenReturn(Collections.emptyList());
        assertNotNull(repository.getSortedPageWithFilters(0, 10, "u.username", "ASC", "user", "email", "ROLE"));
    }

    @Test
    void testGetFiltersCount() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Long.class), anyString(), anyString(), anyString()))
                .thenReturn(5L);
        assertEquals(5L, repository.getFiltersCount("user", "email", "ROLE"));
    }
}
