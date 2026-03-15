package org.derleta.authorization.repository.impl.token;

import org.derleta.authorization.domain.entity.token.TokenEntity;
import org.derleta.authorization.domain.types.TokenTable;
import org.derleta.authorization.repository.TokenRepositoryInterface;
import org.derleta.authorization.repository.sort.SortParameters;
import org.derleta.authorization.utils.ValidatorUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;
import java.util.Locale;
import java.util.Optional;


/**
 * Repository class responsible for managing JWT refresh tokens in the underlying
 * database. It provides functionality to interact with and manipulate refresh token
 * records, including creation, retrieval, updating, and deletion.
 * <p>
 * This class includes methods for:
 * - Counting all refresh tokens in the database.
 * - Retrieving paginated, sorted, and filtered lists of refresh tokens.
 * - Fetching specific tokens based on unique identifiers.
 * - Checking and managing the validity of tokens through expiration dates.
 * - Saving new tokens and managing token lifecycle operations.
 * <p>
 * The class leverages {@code JdbcTemplate} for database interactions
 * and custom row mapping through the {@code TokenMapper} to convert query
 * results into application-level domain objects.
 * <p>
 * All operations are tailored to work with the "refresh_tokens" database table.
 */
@Repository
public non-sealed class RefreshTokenRepository extends TokenRepository implements TokenRepositoryInterface {

    private static final TokenTable tokenTable = TokenTable.REFRESH;

    /**
     * Constructs a new instance of RefreshTokenRepository and initializes the JDBC template
     * with the provided data source.
     *
     * @param dataSource the data source used to configure the JDBC template for database operations
     */
    @Autowired
    public RefreshTokenRepository(DataSource dataSource) {
        super(new JdbcTemplate(dataSource));
    }

    /**
     * Retrieves the total count of JWT refresh tokens stored in the database.
     *
     * @return the total count of JWT refresh tokens as an Integer
     */
    public Integer getSize() {
        String sql = """
                SELECT COUNT(*) FROM refresh_tokens;
                """;
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

    public Optional<TokenEntity> findByJti(final String jti) {
        if (jti == null || jti.isBlank()) {
            return Optional.empty();
        }
        String sql = """
                SELECT u.*, t.*
                FROM users u
                JOIN refresh_tokens t ON u.user_id = t.user_id
                WHERE jti = ?;
                """;
        List<TokenEntity> results = jdbcTemplate.query(sql, new TokenMapper(), jti);
        if (results.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(results.getFirst());
        }
    }

    public List<TokenEntity> getPage(final int offset, final int size) {
        validatePage(offset, size);
        String sql = """
                SELECT u.*, t.*
                FROM users u
                JOIN refresh_tokens t ON u.user_id = t.user_id
                LIMIT ?, ?;
                """;
        return jdbcTemplate.query(sql, new TokenMapper(), offset, size);
    }

    public List<TokenEntity> findValid(final int offset, final int size, final String sortByParam, final String sortOrderParam) {
        validatePage(offset, size);
        String sql = buildQueryForValidTokensPage(sortByParam, sortOrderParam);
        return jdbcTemplate.query(sql, new TokenMapper(), offset, size);
    }

    /**
     * Builds a SQL query for fetching a paginated and sorted list of valid refresh tokens
     * and their associated user details from the database. The method validates the sorting
     * parameters before constructing the query string.
     *
     * @param sortBy    the column name by which the result set should be sorted. Must be a valid
     *                  column included in the allowed set of sort columns.
     * @param sortOrder the sorting order, either "ASC" for ascending or "DESC" for descending.
     *                  Must be included in the allowed set of sort orders.
     * @return a SQL query string for selecting valid refresh tokens and their associated user
     * details. The string includes placeholders for pagination parameters.
     * @throws IllegalArgumentException if the provided sortBy or sortOrder parameter is invalid.
     */
    private static String buildQueryForValidTokensPage(final String sortBy, final String sortOrder) {
        ValidatorUtils.validateSortParameters(sortBy, sortOrder, ALLOWED_SORT_COLUMNS, ALLOWED_SORT_ORDERS);
        String normalizedSortOrder = sortOrder.toUpperCase(Locale.ROOT);
        SortParameters sortParameters = new SortParameters(sortBy, normalizedSortOrder);
        return String.format("""
                    SELECT u.*, t.*
                    FROM users u
                    JOIN refresh_tokens t ON u.user_id = t.user_id
                    WHERE expiration_date > NOW()
                    ORDER BY %s %s
                    LIMIT ?, ?;
                """.formatted(sortParameters.sortBy(), sortParameters.sortOrder()));
    }


    /**
     * Retrieves a TokenEntity object wrapped in an Optional that matches the specified token ID.
     *
     * @param tokenId the unique identifier of the token to be retrieved
     * @return an Optional containing the matching TokenEntity if found, or an empty Optional if no match is found
     */
    public Optional<TokenEntity> findById(final long tokenId) {
        String sql = """
                SELECT u.*, t.*
                FROM users u
                JOIN refresh_tokens t ON u.user_id = t.user_id
                WHERE token_id = ?;
                """;
        List<TokenEntity> results = jdbcTemplate.query(sql, new TokenMapper(), tokenId);
        if (results.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(results.getFirst());
        }
    }

    @Override
    public int save(final long userId, final String token, final java.sql.Timestamp expirationDate,
                    final int version, final String jti, final boolean revoked) {
        return save(tokenTable, userId, token, expirationDate, version, jti, revoked);
    }

    /**
     * Deletes a refresh token entry from the database identified by the specified token ID
     * and user ID.
     *
     * @param tokenId the unique identifier of the token to be deleted
     * @param userId  the unique identifier of the user associated with the token
     * @return the number of rows affected by the delete operation; returns 0 if no matching entry was found
     */
    public int deleteById(final long tokenId, final long userId) {
        String sql = """ 
                DELETE FROM refresh_tokens
                WHERE token_id = ? AND user_id = ?;
                """;
        return jdbcTemplate.update(sql, tokenId, userId);
    }

    public List<TokenEntity> getSortedPageWithFilters(final int offset, final int size, final String sortByParam, final String sortOrderParam, final String username, final String email) {
        validatePage(offset, size);
        String sql = buildQueryForSortedPage(sortByParam, sortOrderParam);
        return jdbcTemplate.query(sql, new TokenMapper(),
                getSqlLikeParam(username), getSqlLikeParam(email),
                offset, size);
    }

    /**
     * Builds a SQL query string for fetching a paginated and sorted list of users and their associated
     * refresh tokens based on the specified sorting parameters. The method validates the provided sorting
     * parameters before constructing the query.
     *
     * @param sortBy    the column name by which the results should be sorted. This parameter must be a valid
     *                  column included in the set of allowed sort columns.
     * @param sortOrder the sorting order, either "ASC" for ascending or "DESC" for descending, which must
     *                  be included in the set of allowed sort orders.
     * @return a SQL query string for selecting and sorting users and their associated tokens, which
     * includes placeholders for pagination and filtering parameters.
     * @throws IllegalArgumentException if the provided sortBy parameter is not valid, or if the
     *                                  sortOrder parameter is not included in the allowed orders.
     */
    private static String buildQueryForSortedPage(final String sortBy, final String sortOrder) {
        ValidatorUtils.validateSortParameters(sortBy, sortOrder, ALLOWED_SORT_COLUMNS, ALLOWED_SORT_ORDERS);
        String normalizedSortOrder = sortOrder.toUpperCase(Locale.ROOT);
        SortParameters sortParameters = new SortParameters(sortBy, normalizedSortOrder);
        return String.format("""
                    SELECT u.*, t.*
                    FROM users u
                    JOIN refresh_tokens t ON u.user_id = t.user_id
                    WHERE u.username LIKE ?
                    AND u.email LIKE ?
                    ORDER BY %s %s
                    LIMIT ?, ?;
                """.formatted(sortParameters.sortBy(), sortParameters.sortOrder()));
    }

    /**
     * Retrieves the count of users and their associated JWT refresh tokens
     * from the database that match the given username and email filters.
     *
     * @param username the username filter to be applied, allowing partial matches
     * @param email    the email filter to be applied, allowing partial matches
     * @return the total count of matching users and their associated tokens as Long
     */
    public Long getFiltersCount(final String username, final String email) {
        String sql = """ 
                SELECT COUNT(*)
                FROM users u
                JOIN refresh_tokens t ON u.user_id = t.user_id
                WHERE u.username LIKE ?
                AND u.email LIKE ?;
                """;
        return jdbcTemplate.queryForObject(sql, Long.class, getSqlLikeParam(username), getSqlLikeParam(email));
    }

    /**
     * Retrieves the count of valid JWT refresh tokens from the database.
     * A token is considered valid if its expiration date is later than the current time.
     *
     * @return the total count of valid JWT refresh tokens as Long
     */
    public Long getValidCount() {
        String sql = """
                SELECT COUNT(*)
                FROM refresh_tokens t
                WHERE expiration_date > NOW();
                """;
        return jdbcTemplate.queryForObject(sql, Long.class);
    }

    public boolean isActive(final String refreshJti, final long userId, final int refreshVer) {
        String sql = """
                SELECT EXISTS(
                    SELECT 1
                    FROM refresh_tokens t
                    WHERE t.jti = ?
                      AND t.user_id = ?
                      AND t.version = ?
                      AND t.revoked = false
                      AND t.expiration_date > NOW()
                )
                """;

        var result = jdbcTemplate.queryForObject(sql, Boolean.class, refreshJti, userId, refreshVer);
        return Boolean.TRUE.equals(result);
    }

    public boolean revokeByJti(final String refreshJti, final long userId, final int refreshVer) {
        if (refreshJti == null || refreshJti.isBlank()) {
            return false;
        }
        String sql = """
                UPDATE refresh_tokens
                SET revoked = true
                WHERE jti = ?
                  AND user_id = ?
                  AND version = ?
                  AND revoked = false;
                """;

        return jdbcTemplate.update(sql, refreshJti, userId, refreshVer) == 1;
    }

    public int revokeAllByUserId(final Long userId) {
        final String sql = """
                UPDATE refresh_tokens
                SET revoked = true
                WHERE user_id = ?
                  AND revoked = false;
                """;
        return jdbcTemplate.update(sql, userId);
    }

    private static void validatePage(final int offset, final int size) {
        if (offset < 0) {
            throw new IllegalArgumentException("offset must be >= 0");
        }
        if (size <= 0) {
            throw new IllegalArgumentException("size must be > 0");
        }
    }
}
