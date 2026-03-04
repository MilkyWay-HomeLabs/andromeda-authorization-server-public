package org.derleta.authorization.security.store;

import org.derleta.authorization.domain.entity.token.RefreshTokenIncidentEntity;
import org.derleta.authorization.repository.RepositoryClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;

@Repository
public class RefreshTokenIncidentStore implements RepositoryClass {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public RefreshTokenIncidentStore(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Transactional
    public void save(final RefreshTokenIncidentEntity incident) {
        if (incident == null) {
            return;
        }

        final String sql = """
                INSERT INTO refresh_token_incidents
                    (token_id, user_id, jti, version, incident_time, ip_address, user_agent, description)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;

        jdbcTemplate.update(
                sql,
                incident.getTokenId(),
                incident.getUserId(),
                incident.getJti(),
                incident.getVersion(),
                incident.getIncidentTime(),
                incident.getIpAddress(),
                incident.getUserAgent(),
                incident.getDescription()
        );
    }

}
