package org.derleta.authorization.config.db;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class DbConfTest {

    @Mock
    private DataSource dataSource;

    @Test
    void testJdbcTemplateBeanCreation() {
        // Given
        DbConf dbConf = new DbConf(dataSource);

        // When
        JdbcTemplate jdbcTemplate = dbConf.jdbcTemplate();

        // Then
        assertNotNull(jdbcTemplate, "JdbcTemplate should not be null");
        assertEquals(dataSource, jdbcTemplate.getDataSource(), "JdbcTemplate should be configured with the provided DataSource");
    }
}
