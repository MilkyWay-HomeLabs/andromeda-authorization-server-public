package org.derleta.authorization.security.store;

import org.derleta.authorization.domain.entity.token.RefreshTokenIncidentEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

class RefreshTokenIncidentStoreTest {

    private RefreshTokenIncidentStore store;

    @Mock
    private DataSource dataSource;

    @Mock
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        // We need to bypass the constructor's new JdbcTemplate(dataSource) if we want to inject our mock
        // But since we can't easily do that without refactoring or using PowerMock (which we shouldn't)
        // Let's use reflection to inject the mock jdbcTemplate
        store = new RefreshTokenIncidentStore(dataSource);

        java.lang.reflect.Field field = RefreshTokenIncidentStore.class.getDeclaredField("jdbcTemplate");
        field.setAccessible(true);
        field.set(store, jdbcTemplate);
    }

    @Test
    void testSave_Successful() {
        // Given
        RefreshTokenIncidentEntity incident = new RefreshTokenIncidentEntity();
        incident.setTokenId(1L);
        incident.setUserId(2L);
        incident.setJti("test-jti");
        incident.setVersion(1);
        Timestamp now = Timestamp.valueOf(LocalDateTime.now());
        incident.setIncidentTime(now);
        incident.setIpAddress("127.0.0.1");
        incident.setUserAgent("Mozilla/5.0");
        incident.setDescription("Suspicious activity");

        // When
        store.save(incident);

        // Then
        String expectedSql = """
                INSERT INTO refresh_token_incidents
                    (token_id, user_id, jti, version, incident_time, ip_address, user_agent, description)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;

        verify(jdbcTemplate).update(
                eq(expectedSql),
                eq(1L),
                eq(2L),
                eq("test-jti"),
                eq(1),
                eq(now),
                eq("127.0.0.1"),
                eq("Mozilla/5.0"),
                eq("Suspicious activity")
        );
    }

    @Test
    void testSave_NullIncident() {
        // When
        store.save(null);

        // Then
        verifyNoInteractions(jdbcTemplate);
    }
}
