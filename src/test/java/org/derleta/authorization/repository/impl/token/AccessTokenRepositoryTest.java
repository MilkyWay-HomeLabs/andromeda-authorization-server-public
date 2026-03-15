package org.derleta.authorization.repository.impl.token;

import org.derleta.authorization.domain.entity.token.TokenEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.test.util.ReflectionTestUtils;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class AccessTokenRepositoryTest {

    @Mock
    private DataSource dataSource;

    @Mock
    private JdbcTemplate jdbcTemplate;

    private AccessTokenRepository repository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        repository = new AccessTokenRepository(dataSource);
        ReflectionTestUtils.setField(repository, "jdbcTemplate", jdbcTemplate);
    }

    @Test
    void testGetSize() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class))).thenReturn(10);
        Integer size = repository.getSize();
        assertEquals(10, size);
        verify(jdbcTemplate).queryForObject(contains("COUNT(*)"), eq(Integer.class));
    }

    @Test
    void testGetPage() {
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), anyInt(), anyInt()))
                .thenReturn(Collections.emptyList());
        List<TokenEntity> result = repository.getPage(0, 10);
        assertNotNull(result);
        verify(jdbcTemplate).query(contains("LIMIT ?, ?;"), any(RowMapper.class), eq(0), eq(10));
    }

    @Test
    void testFindValid() {
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), anyInt(), anyInt()))
                .thenReturn(Collections.emptyList());
        List<TokenEntity> result = repository.findValid(0, 10, "t.created_at", "DESC");
        assertNotNull(result);
        verify(jdbcTemplate).query(contains("expiration_date > NOW()"), any(RowMapper.class), eq(0), eq(10));
    }

    @Test
    void testFindByJti() {
        when(jdbcTemplate.queryForObject(anyString(), any(RowMapper.class), anyString()))
                .thenReturn(mock(TokenEntity.class));
        Optional<TokenEntity> result = repository.findByJti("some-jti");
        assertTrue(result.isPresent());
        verify(jdbcTemplate).queryForObject(contains("t.jti = ?;"), any(RowMapper.class), eq("some-jti"));
    }

    @Test
    void testFindById() {
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), anyLong()))
                .thenReturn(Collections.emptyList());
        Optional<TokenEntity> result = repository.findById(1L);
        assertFalse(result.isPresent());
        verify(jdbcTemplate).query(contains("token_id = ?"), any(RowMapper.class), eq(1L));
    }

    @Test
    void testSave() {
        Timestamp now = Timestamp.from(Instant.now());
        when(jdbcTemplate.update(anyString(), any(), any(), any(), any(), any(), any())).thenReturn(1);
        int result = repository.save(1L, "token", now, 1, "jti", false);
        assertEquals(1, result);
        verify(jdbcTemplate).update(contains("INSERT INTO ACCESS"), eq(1L), eq("token"), eq(now), eq(1), eq("jti"), eq(false));
    }

    @Test
    void testSave_DuplicateKey() {
        Timestamp now = Timestamp.from(Instant.now());
        when(jdbcTemplate.update(anyString(), any(), any(), any(), any(), any(), any()))
                .thenThrow(new DuplicateKeyException("duplicate"));
        int result = repository.save(1L, "token", now, 1, "jti", false);
        assertEquals(0, result);
    }

    @Test
    void testDeleteById() {
        when(jdbcTemplate.update(anyString(), anyLong(), anyLong())).thenReturn(1);
        int result = repository.deleteById(1L, 2L);
        assertEquals(1, result);
        verify(jdbcTemplate).update(contains("DELETE FROM access_tokens"), eq(1L), eq(2L));
    }

    @Test
    void testGetSortedPageWithFilters() {
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), any(), any(), any(), any()))
                .thenReturn(Collections.emptyList());
        List<TokenEntity> result = repository.getSortedPageWithFilters(0, 10, "u.username", "ASC", "user", "email");
        assertNotNull(result);
        verify(jdbcTemplate).query(anyString(), any(RowMapper.class), eq("%user%"), eq("%email%"), eq(0), eq(10));
    }

    @Test
    void testGetFiltersCount() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Long.class), any(), any())).thenReturn(5L);
        Long count = repository.getFiltersCount("user", "email");
        assertEquals(5L, count);
        verify(jdbcTemplate).queryForObject(anyString(), eq(Long.class), eq("%user%"), eq("%email%"));
    }

    @Test
    void testGetValidCount() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Long.class))).thenReturn(3L);
        Long count = repository.getValidCount();
        assertEquals(3L, count);
        verify(jdbcTemplate).queryForObject(anyString(), eq(Long.class));
    }

    @Test
    void testRevokeAllByUserId() {
        when(jdbcTemplate.update(anyString(), anyLong())).thenReturn(2);
        int result = repository.revokeAllByUserId(1L);
        assertEquals(2, result);
        verify(jdbcTemplate).update(contains("UPDATE access_tokens"), eq(1L));
    }
}
