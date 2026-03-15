package org.derleta.authorization.repository.impl;

import org.derleta.authorization.domain.entity.UserEntity;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class UserRepositoryTest {

    @Mock
    private DataSource dataSource;

    @Mock
    private JdbcTemplate jdbcTemplate;

    private UserRepository repository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        repository = new UserRepository(dataSource);
        ReflectionTestUtils.setField(repository, "jdbcTemplate", jdbcTemplate);
    }

    @Test
    void testGetSize() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class))).thenReturn(10);
        assertEquals(10, repository.getSize());
    }

    @Test
    void testFindById() {
        UserEntity user = new UserEntity();
        when(jdbcTemplate.queryForObject(anyString(), any(RowMapper.class), anyLong())).thenReturn(user);
        assertEquals(user, repository.findById(1L));
    }

    @Test
    void testFindByEmail() {
        UserEntity user = new UserEntity();
        when(jdbcTemplate.queryForObject(anyString(), any(RowMapper.class), anyString())).thenReturn(user);
        assertEquals(user, repository.findByEmail("test@example.com"));
    }

    @Test
    void testIsBlocked() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Boolean.class), anyLong())).thenReturn(true);
        assertTrue(repository.isBlocked(1L));
    }

    @Test
    void testIsVerified() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Boolean.class), anyLong())).thenReturn(false);
        assertFalse(repository.isVerified(1L));
    }

    @Test
    void testGetTokenVersion() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), anyLong())).thenReturn(5);
        assertEquals(5, repository.getTokenVersion(1L));
    }

    @Test
    void testSave() {
        UserEntity user = new UserEntity();
        user.setUsername("user");
        user.setEmail("email");
        user.setPassword("pass");

        when(jdbcTemplate.update(any(org.springframework.jdbc.core.PreparedStatementCreator.class), any(org.springframework.jdbc.support.KeyHolder.class)))
                .thenAnswer(invocation -> {
                    org.springframework.jdbc.support.KeyHolder kh = invocation.getArgument(1);
                    ReflectionTestUtils.setField(kh, "keyList", List.of(Collections.singletonMap("user_id", 1L)));
                    return 1;
                });

        assertEquals(1L, repository.save(user));
    }

    @Test
    void testUpdate() {
        UserEntity user = new UserEntity();
        user.setUsername("user");
        user.setEmail("email");
        user.setVerified(true);
        user.setBlocked(false);
        when(jdbcTemplate.update(anyString(), any(), any(), any(), any(), anyLong())).thenReturn(1);
        assertEquals(1, repository.update(1L, user));
    }

    @Test
    void testDeleteById() {
        when(jdbcTemplate.update(anyString(), anyLong())).thenReturn(1);
        assertEquals(1, repository.deleteById(1L));
    }

    @Test
    void testIsEmailExist() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), anyString())).thenReturn(1);
        assertTrue(repository.isEmailExist("test@example.com"));
    }

    @Test
    void testIsLoginExist() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), anyString())).thenReturn(0);
        assertFalse(repository.isLoginExist("user"));
    }

    @Test
    void testUnlock() {
        repository.unlock(1L);
        verify(jdbcTemplate).update(anyString(), eq(1L));
    }

    @Test
    void testUpdateStatus() {
        repository.updateStatus(1L, true, false);
        verify(jdbcTemplate).update(anyString(), eq(true), eq(false), eq(1L));
    }

    @Test
    void testUpdatePassword() {
        when(jdbcTemplate.update(anyString(), anyString(), anyLong())).thenReturn(1);
        assertEquals(1, repository.updatePassword(1L, "newPass"));
    }

    @Test
    void testIncrementTokenVersion() {
        when(jdbcTemplate.update(anyString(), anyLong())).thenReturn(1);
        assertEquals(1, repository.incrementTokenVersion(1L));
    }
}
