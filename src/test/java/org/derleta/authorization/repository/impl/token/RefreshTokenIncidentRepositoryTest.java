package org.derleta.authorization.repository.impl.token;

import org.derleta.authorization.domain.entity.token.RefreshTokenIncidentEntity;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class RefreshTokenIncidentRepositoryTest {

    @Mock
    private DataSource dataSource;

    @Mock
    private JdbcTemplate jdbcTemplate;

    private RefreshTokenIncidentRepository repository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        repository = new RefreshTokenIncidentRepository(dataSource);
        ReflectionTestUtils.setField(repository, "jdbcTemplate", jdbcTemplate);
    }

    @Test
    void testCountAll() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Long.class))).thenReturn(42L);
        assertEquals(42L, repository.countAll());
    }

    @Test
    void testFindPage() {
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), anyInt(), anyInt()))
                .thenReturn(Collections.emptyList());
        List<RefreshTokenIncidentEntity> result = repository.findPage(1, 10);
        assertNotNull(result);
        verify(jdbcTemplate).query(anyString(), any(RowMapper.class), eq(10), eq(10));
    }

    @Test
    void testFindByDateFrom() {
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), any(), anyInt()))
                .thenReturn(Collections.emptyList());
        List<RefreshTokenIncidentEntity> result = repository.findByDateFrom(Instant.now(), 5);
        assertNotNull(result);
        verify(jdbcTemplate).query(anyString(), any(RowMapper.class), any(Timestamp.class), eq(5));
    }

    @Test
    void testFindById() {
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), anyLong()))
                .thenReturn(Collections.emptyList());
        Optional<RefreshTokenIncidentEntity> result = repository.findById(1L);
        assertFalse(result.isPresent());
    }
}
