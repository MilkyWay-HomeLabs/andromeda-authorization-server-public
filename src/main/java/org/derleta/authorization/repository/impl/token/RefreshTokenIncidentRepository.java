package org.derleta.authorization.repository.impl.token;

import org.derleta.authorization.domain.entity.token.RefreshTokenIncidentEntity;
import org.derleta.authorization.repository.RepositoryClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public class RefreshTokenIncidentRepository implements RepositoryClass {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public RefreshTokenIncidentRepository(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }


    public long countAll() {
        final String sql = "SELECT COUNT(*) FROM refresh_token_incidents";
        final Long value = jdbcTemplate.queryForObject(sql, Long.class);
        return value == null ? 0L : value;
    }

    public List<RefreshTokenIncidentEntity> findPage(final int page, final int size) {
        final int safePage = Math.max(0, page);
        final int safeSize = Math.max(1, Math.min(200, size));
        final int offset = safePage * safeSize;

        final String sql = """
                SELECT id, token_id, user_id, jti, version, incident_time, ip_address, user_agent, description
                FROM refresh_token_incidents
                ORDER BY incident_time DESC, id DESC
                LIMIT ? OFFSET ?
                """;

        return jdbcTemplate.query(sql, rowMapper(), safeSize, offset);
    }

    public List<RefreshTokenIncidentEntity> findByDateFrom(final Instant fromInclusive, final int limit) {
        final int safeLimit = Math.max(1, Math.min(5000, limit));

        final String sql = """
                SELECT id, token_id, user_id, jti, version, incident_time, ip_address, user_agent, description
                FROM refresh_token_incidents
                WHERE incident_time >= ?
                ORDER BY incident_time DESC, id DESC
                LIMIT ?
                """;

        return jdbcTemplate.query(sql, rowMapper(), Timestamp.from(fromInclusive), safeLimit);
    }

    public Optional<RefreshTokenIncidentEntity> findById(final long id) {
        final String sql = """
                SELECT id, token_id, user_id, jti, version, incident_time, ip_address, user_agent, description
                FROM refresh_token_incidents
                WHERE id = ?
                """;

        final List<RefreshTokenIncidentEntity> rows = jdbcTemplate.query(sql, rowMapper(), id);
        return rows.stream().findFirst();
    }

    private RowMapper<RefreshTokenIncidentEntity> rowMapper() {
        return (rs, rowNum) -> {
            final RefreshTokenIncidentEntity i = new RefreshTokenIncidentEntity();
            i.setId(rs.getLong("id"));

            final Object tokenIdObj = rs.getObject("token_id");
            i.setTokenId(tokenIdObj == null ? null : ((Number) tokenIdObj).longValue());

            i.setUserId(rs.getLong("user_id"));
            i.setJti(rs.getString("jti"));
            i.setVersion(rs.getInt("version"));

            final Timestamp ts = rs.getTimestamp("incident_time");
            i.setIncidentTime(ts == null ? null : Timestamp.from(ts.toInstant()));

            i.setIpAddress(rs.getString("ip_address"));
            i.setUserAgent(rs.getString("user_agent"));
            i.setDescription(rs.getString("description"));
            return i;
        };
    }

}
