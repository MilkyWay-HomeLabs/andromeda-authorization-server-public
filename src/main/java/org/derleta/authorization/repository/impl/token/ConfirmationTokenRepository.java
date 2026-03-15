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
 * The ConfirmationTokenRepository class provides access to and management of confirmation token data
 * within a relational database. It extends the base functionality from the TokenRepository class
 * and utilizes {@link JdbcTemplate} for executing SQL operations. It supports various operations
 * such as retrieving, saving, updating, filtering, and deleting confirmation tokens. It also performs
 * related queries for pagination and sorting on confirmation tokens.
 * <p>
 * Note: This repository assumes the existence of two database tables:
 * 1. `confirmation_tokens` - stores confirmation tokens.
 * 2. `users` - stores user details.
 * <p>
 * All SQL queries are performed using standard conventions for the respective database schema.
 * <p>
 * Key functionalities offered:
 * - Count tokens or users that match specific criteria.
 * - Perform paginated and sorted queries with optional filters.
 * - Save, update, or delete confirmation tokens in the database.
 * - Mark tokens as expired or retrieve the valid token count.
 */
@Repository
public non-sealed class ConfirmationTokenRepository extends TokenRepository implements TokenRepositoryInterface {

    private static final TokenTable tokenTable = TokenTable.CONFIRMATION;

    /**
     * Constructs a new ConfirmationTokenRepository with the specified DataSource.
     * This constructor initializes the internal JdbcTemplate instance.
     *
     * @param dataSource the DataSource to be used by the JdbcTemplate for database
     *                   operations; must not be null
     */
    @Autowired
    public ConfirmationTokenRepository(DataSource dataSource) {
        super(new JdbcTemplate(dataSource));
    }

    /**
     * Retrieves the total number of records in the confirmation_tokens table.
     *
     * @return the total count of confirmation tokens as an Integer
     */
    public Integer getSize() {
        String sql = """
                SELECT COUNT(*) FROM confirmation_tokens;
                """;
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

    /**
     * Retrieves a paginated list of token entities from the database.
     * This method executes a SQL query to join user and token information,
     * mapping the results to TokenEntity objects using a TokenMapper instance.
     *
     * @param offset the starting position of the records to retrieve
     * @param size   the maximum number of records to retrieve
     * @return a list of token entities corresponding to the specified offset and size, or an empty list if no results are found
     */
    public List<TokenEntity> getPage(final int offset, final int size) {
        String sql = """ 
                SELECT u.*, t.*
                FROM users u
                JOIN confirmation_tokens t ON u.user_id = t.user_id
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
     * Constructs an SQL query for retrieving a paginated list of users and their associated valid confirmation tokens.
     * The query applies filtering to include only tokens that have not expired and enables sorting
     * based on the specified column and order. Pagination parameters are provided as placeholders in the query.
     *
     * @param sortBy    the column to sort the results by. Must be a valid column from the allowed sort columns.
     * @param sortOrder the sort order for the query (e.g., "ASC" or "DESC"). Must be a valid sorting order from the allowed sort orders.
     * @return a formatted SQL query string with placeholders for sorting and pagination parameters.
     * @throws IllegalArgumentException if the provided sorting parameters are invalid.
     */
    private static String buildQueryForValidTokensPage(final String sortBy, final String sortOrder) {
        ValidatorUtils.validateSortParameters(sortBy, sortOrder, ALLOWED_SORT_COLUMNS, ALLOWED_SORT_ORDERS);
        String normalizedSortOrder = sortOrder.toUpperCase(Locale.ROOT);
        SortParameters sortParameters = new SortParameters(sortBy, normalizedSortOrder);
        return String.format("""
                    SELECT u.*, t.*
                    FROM users u
                    JOIN confirmation_tokens t ON u.user_id = t.user_id
                    WHERE expiration_date > NOW()
                    ORDER BY %s %s
                    LIMIT ?, ?;
                """.formatted(sortParameters.sortBy(), sortParameters.sortOrder()));
    }

    /**
     * Finds and retrieves a {@link TokenEntity} based on the provided token ID.
     * This method queries the database for a token and its associated user
     * information. If a matching token is found, it is returned wrapped in an
     * {@link Optional}; otherwise, an empty {@link Optional} is returned.
     *
     * @param tokenId the unique identifier of the token to retrieve
     * @return an {@link Optional} containing the {@link TokenEntity} if found,
     * or an empty {@link Optional} if no matching record exists
     */
    public Optional<TokenEntity> findById(final long tokenId) {
        String sql = """
                SELECT u.*, t.*
                FROM users u
                JOIN confirmation_tokens t ON u.user_id = t.user_id
                WHERE token_id = ?;
                """;
        List<TokenEntity> results = jdbcTemplate.query(sql, new TokenMapper(), tokenId);
        if (results.isEmpty()) return Optional.empty();
        return Optional.of(results.getFirst());
    }

    public Optional<TokenEntity> findByJti(final String jti) {
        if (jti == null || jti.isBlank()) {
            return Optional.empty();
        }
        String sql = """
                SELECT u.*, t.*
                FROM users u
                JOIN confirmation_tokens t ON u.user_id = t.user_id
                WHERE jti = ?;
                """;
        List<TokenEntity> results = jdbcTemplate.query(sql, new TokenMapper(), jti);
        if (results.isEmpty()) return Optional.empty();
        return Optional.of(results.getFirst());
    }

    @Override
    public int save(final long userId, final String token, final java.sql.Timestamp expirationDate,
                    final int version, final String jti, boolean revoked) {
        return save(tokenTable, userId, token, expirationDate, version, jti, revoked);
    }

    /**
     * Deletes a confirmation token record from the database based on the specified token ID and user ID.
     *
     * @param tokenId the unique identifier of the token to be deleted
     * @param userId  the unique identifier of the user associated with the token
     * @return the number of rows affected by the delete operation
     */
    public int deleteById(final long tokenId, final long userId) {
        String sql = """ 
                DELETE FROM confirmation_tokens
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
     * Constructs an SQL query for retrieving a sorted and paginated list of users and their associated confirmation tokens.
     * The query includes filtering by username and email patterns and enables sorting on specified columns and orders.
     * The SQL query is dynamically built based on validated sorting parameters.
     *
     * @param sortBy    the column to sort the results by. Must be a valid column from the allowed sort columns.
     * @param sortOrder the sort order for the query (e.g., "ASC" or "DESC"). Must be a valid sorting order from the allowed sort orders.
     * @return a formatted SQL query string with placeholders for filtering, ordering, and pagination parameters.
     * @throws IllegalArgumentException if the provided sorting parameters are invalid.
     */
    private static String buildQueryForSortedPage(final String sortBy, final String sortOrder) {
        ValidatorUtils.validateSortParameters(sortBy, sortOrder, ALLOWED_SORT_COLUMNS, ALLOWED_SORT_ORDERS);
        String normalizedSortOrder = sortOrder.toUpperCase(Locale.ROOT);
        SortParameters sortParameters = new SortParameters(sortBy, normalizedSortOrder);
        return String.format("""
                    SELECT u.*, t.*
                    FROM users u
                    JOIN confirmation_tokens t ON u.user_id = t.user_id
                    WHERE u.username LIKE ?
                    AND u.email LIKE ?
                    ORDER BY %s %s
                    LIMIT ?, ?;
                """.formatted(sortParameters.sortBy(), sortParameters.sortOrder()));
    }

    /**
     * Retrieves the count of users who match the specified username and email filters.
     * This method performs a database query, counting the rows where the username and email
     * match the provided filter conditions.
     *
     * @param username the username filter to match, supporting partial matches (e.g., using wildcards)
     * @param email    the email filter to match, supporting partial matches (e.g., using wildcards)
     * @return the total count of matching users as a Long
     */
    public Long getFiltersCount(final String username, final String email) {
        String sql = """ 
                SELECT COUNT(*)
                FROM users u
                JOIN confirmation_tokens t ON u.user_id = t.user_id
                WHERE u.username LIKE ?
                AND u.email LIKE ?;
                """;
        return jdbcTemplate.queryForObject(sql, Long.class, getSqlLikeParam(username), getSqlLikeParam(email));
    }

    /**
     * Retrieves the count of unexpired confirmation tokens from the database.
     * This method executes a query to count the rows in the `confirmation_tokens` table
     * where the `expiration_date` is greater than the current time.
     *
     * @return the number of valid confirmation tokens as a Long
     */
    public Long getValidCount() {
        String sql = """
                SELECT COUNT(*)
                FROM confirmation_tokens t
                WHERE expiration_date > NOW();
                """;
        return jdbcTemplate.queryForObject(sql, Long.class);
    }

    /**
     * Updates the expiration date of a confirmation token to the current timestamp.
     * This method marks the token associated with the given token ID as expired
     * in the confirmation_tokens table of the database.
     *
     * @param tokenId the unique identifier of the confirmation token to be updated
     */
    public int setExpired(final long tokenId) {
        String sql = """
                UPDATE confirmation_tokens
                SET expiration_date = NOW()
                WHERE token_id = ?;
                """;
        return jdbcTemplate.update(sql, tokenId);
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
