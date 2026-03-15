package org.derleta.authorization.repository.impl.token;

import org.derleta.authorization.domain.entity.token.TokenEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.test.util.ReflectionTestUtils;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RefreshTokenRepositoryTest {

    @Mock
    private DataSource dataSource;

    @Mock
    private JdbcTemplate jdbcTemplate;

    private RefreshTokenRepository repository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        repository = new RefreshTokenRepository(dataSource);
        ReflectionTestUtils.setField(repository, "jdbcTemplate", jdbcTemplate);
    }

    @Test
    void testGetSize() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class))).thenReturn(8);
        assertEquals(8, repository.getSize());
    }

    @Test
    void testFindByJti() {
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), anyString()))
                .thenReturn(List.of(mock(TokenEntity.class)));
        assertTrue(repository.findByJti("jti").isPresent());
    }

    @Test
    void testGetPage() {
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), anyInt(), anyInt()))
                .thenReturn(Collections.emptyList());
        assertNotNull(repository.getPage(0, 10));
    }

    @Test
    void testFindValid() {
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), anyInt(), anyInt()))
                .thenReturn(Collections.emptyList());
        assertNotNull(repository.findValid(0, 10, "t.created_at", "DESC"));
    }

    @Test
    void testFindById() {
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), anyLong()))
                .thenReturn(Collections.emptyList());
        assertFalse(repository.findById(1L).isPresent());
    }

    @Test
    void testSave() {
        Timestamp now = Timestamp.from(Instant.now());
        when(jdbcTemplate.update(anyString(), any(), any(), any(), any(), any(), any())).thenReturn(1);
        assertEquals(1, repository.save(1L, "token", now, 1, "jti", false));
    }

    @Test
    void testDeleteById() {
        when(jdbcTemplate.update(anyString(), anyLong(), anyLong())).thenReturn(1);
        assertEquals(1, repository.deleteById(1L, 2L));
    }

    @Test
    void testIsActive() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Boolean.class), anyString(), anyLong(), anyInt()))
                .thenReturn(true);
        assertTrue(repository.isActive("jti", 1L, 1));
    }

    @Test
    void testRevokeByJti() {
        when(jdbcTemplate.update(anyString(), anyString(), anyLong(), anyInt())).thenReturn(1);
        assertTrue(repository.revokeByJti("jti", 1L, 1));
    }

    @Test
    void testRevokeAllByUserId() {
        when(jdbcTemplate.update(anyString(), anyLong())).thenReturn(3);
        assertEquals(3, repository.revokeAllByUserId(1L));
    }
}
