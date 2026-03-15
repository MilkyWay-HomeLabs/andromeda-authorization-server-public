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
import static org.mockito.Mockito.*;

class ConfirmationTokenRepositoryTest {

    @Mock
    private DataSource dataSource;

    @Mock
    private JdbcTemplate jdbcTemplate;

    private ConfirmationTokenRepository repository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        repository = new ConfirmationTokenRepository(dataSource);
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
        verify(jdbcTemplate).query(contains("LIMIT ?, ?"), any(RowMapper.class), eq(0), eq(10));
    }

    @Test
    void testFindValid() {
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), anyInt(), anyInt()))
                .thenReturn(Collections.emptyList());
        assertNotNull(repository.findValid(0, 10, "t.created_at", "ASC"));
    }

    @Test
    void testFindById() {
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), anyLong()))
                .thenReturn(Collections.emptyList());
        assertFalse(repository.findById(1L).isPresent());
    }

    @Test
    void testFindByJti() {
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), anyString()))
                .thenReturn(List.of(mock(TokenEntity.class)));
        assertTrue(repository.findByJti("jti").isPresent());
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
    void testSetExpired() {
        when(jdbcTemplate.update(anyString(), anyLong())).thenReturn(1);
        assertEquals(1, repository.setExpired(1L));
        verify(jdbcTemplate).update(contains("UPDATE confirmation_tokens"), eq(1L));
    }

    @Test
    void testGetFiltersCount() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Long.class), any(), any())).thenReturn(2L);
        assertEquals(2L, repository.getFiltersCount("u", "e"));
    }

    @Test
    void testGetValidCount() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Long.class))).thenReturn(1L);
        assertEquals(1L, repository.getValidCount());
    }
}
