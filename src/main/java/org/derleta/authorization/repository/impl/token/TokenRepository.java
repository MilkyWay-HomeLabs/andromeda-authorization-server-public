package org.derleta.authorization.repository.impl.token;

import org.derleta.authorization.domain.builder.TokenFactory;
import org.derleta.authorization.domain.entity.UserEntity;
import org.derleta.authorization.domain.entity.token.TokenEntity;
import org.derleta.authorization.domain.types.TokenTable;
import org.derleta.authorization.domain.types.TokenType;
import org.derleta.authorization.repository.RepositoryClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Set;

public sealed abstract class TokenRepository implements RepositoryClass permits RefreshTokenRepository, AccessTokenRepository, ConfirmationTokenRepository {

    protected static final Set<String> ALLOWED_SORT_COLUMNS = Set.of("u.user_id", "u.username", "u.email", "t.created_at", "t.expiration_date", "t.token_id", "t.version");
    protected static final Set<String> ALLOWED_SORT_ORDERS = Set.of("ASC", "DESC");

    protected final JdbcTemplate jdbcTemplate;
    protected final Logger logger;

    @Autowired
    protected TokenRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.logger = LoggerFactory.getLogger(getClass());
    }

    protected int save(TokenTable tokenTable, long userId, String token, Timestamp expirationDate, int version, String jti, boolean revoked) {
        if (tokenTable == null
                || token == null || token.isBlank()
                || jti == null || jti.isBlank()
                || expirationDate == null) {
            logger.warn("Invalid input data for saving token: userId={}, token={}, expirationDate={}, version={}, jti={}",
                    userId, token, expirationDate, version, jti);
            return 0;
        }

        final String sql = """
                INSERT INTO %s (user_id, token, expiration_date, version, jti, revoked)
                VALUES (?, ?, ?, ?, ?, ?);
                """.formatted(tokenTable.name());

        try {
            return jdbcTemplate.update(sql, userId, token, expirationDate, version, jti, revoked);
        } catch (DuplicateKeyException e) {
            logger.error("Duplicate key exception while saving token: userId={}, token={}, expirationDate={}, version={}, jti={}",
                    userId, token, expirationDate, version, jti, e);
            return 0;
        }
    }


    /**
     * Constructs a SQL "LIKE" parameter with wildcard characters (%) appended
     * to both the beginning and the end of the provided string parameter.
     * If the input parameter is null, it returns a string with only
     * wildcard characters ("%%").
     *
     * @param param the input string to be wrapped with wildcards;
     *              can be null, in which case an empty string is substituted
     * @return a string formatted as "%<input>%" for SQL "LIKE" queries
     */
    protected String getSqlLikeParam(String param) {
        final String effectiveParam = (param != null) ? param : "";
        return "%" + effectiveParam + "%";
    }

    /**
     * A {@link RowMapper} implementation responsible for mapping rows of a {@link ResultSet}
     * to {@link TokenEntity} objects. This class extracts user data and token data from
     * the result set and constructs the corresponding objects, linking them as required.
     * <p>
     * The mapping process includes reading user-related columns (e.g., user_id, username, email, password)
     * and token-related columns (e.g., token_id, token, expiration_date, type). A {@link UserEntity} is
     * created to represent the user, and a specific implementation of {@link TokenEntity} is created
     * based on the token type using the {@link TokenFactory#createToken} method.
     * <p>
     * Responsibilities:
     * - Extract user details and construct a {@link UserEntity}.
     * - Extract token details and determine the token type based on the database column value.
     * - Utilize {@link TokenFactory} to create the appropriate token entity.
     * <p>
     * Exception Handling:
     * - Throws {@link SQLException} if there are errors accessing the {@link ResultSet}.
     * <p>
     * This class is typically used in conjunction with JDBC templates for database-to-entity mapping.
     */
    public static class TokenMapper implements RowMapper<TokenEntity> {
        @Override
        public TokenEntity mapRow(ResultSet resultSet, int i) throws SQLException {
            long userId = resultSet.getLong("u.user_id");
            long tokenId = resultSet.getLong("token_id");
            String token = resultSet.getString("t.token");
            Timestamp expirationDate = resultSet.getTimestamp("t.expiration_date");
            int version = resultSet.getInt("t.version");
            String jti = resultSet.getString("t.jti");
            TokenType tokenType = TokenType.valueOf(resultSet.getString("t.type"));
            boolean revoked = resultSet.getBoolean("t.revoked");
            return TokenFactory.createToken(tokenType, tokenId, userId, token, expirationDate, version, jti, revoked);
        }
    }

    /**
     * Implementation of the {@link RowMapper} interface to map a single column value
     * from a database result set to a {@code String}.
     * <p>
     * This class is specifically used to extract the "token" column from the result set
     * of a query on the `andromeda.jwt_tokens` table. It is designed to be a reusable
     * mapper for mapping the "jt.token" column to a string value.
     * <p>
     * Usage typically occurs within the context of DAO or repository methods, where
     * the result set of a query needs to be mapped to a single string value.
     */
    public static class JwtTokenMapper implements RowMapper<String> {
        @Override
        public String mapRow(ResultSet resultSet, int i) throws SQLException {
            return resultSet.getString("jt.token");
        }
    }

}
