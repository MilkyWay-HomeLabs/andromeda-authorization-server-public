package org.derleta.authorization.repository.impl;

import org.derleta.authorization.domain.entity.UserEntity;
import org.derleta.authorization.repository.RepositoryClass;
import org.derleta.authorization.repository.sort.SortParameters;
import org.derleta.authorization.utils.StringUtils;
import org.derleta.authorization.utils.ValidatorUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Responsible for interacting with the `users` table in the database to perform CRUD operations.
 * Provides methods for accessing and manipulating user data, such as retrieving paginated user lists,
 * checking user status, and applying filters to user queries.
 */
@Repository
public class UserRepository implements RepositoryClass {

    private static final Set<String> ALLOWED_SORT_COLUMNS = Set.of("user_id", "username", "email", "created_at");
    private static final Set<String> ALLOWED_SORT_ORDERS = Set.of("ASC", "DESC");

    private final JdbcTemplate jdbcTemplate;

    /**
     * Constructs a new instance of UserRepository.
     * Initializes the JdbcTemplate with the provided DataSource to allow interaction with the database.
     *
     * @param dataSource the DataSource object used to configure the database connection
     */
    @Autowired
    public UserRepository(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /**
     * Retrieves the total number of user records in the database.
     *
     * @return the total count of users as an Integer
     */
    public Integer getSize() {
        String sql = """
                SELECT COUNT(*) FROM users;
                """;
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

    /**
     * Retrieves a paginated list of UserEntity records from the database.
     * The method fetches a specific number of records starting from a given offset.
     *
     * @param offset the starting point for pagination, indicating the number of records to skip
     * @param size   the maximum number of records to retrieve in the result set
     * @return a list of UserEntity objects representing the retrieved records
     */
    public List<UserEntity> getPage(final int offset, final int size) {
        String sql = """ 
                SELECT user_id, username, password, email, created_at, updated_at, verified, blocked, token_version
                FROM users
                LIMIT ?, ?;
                """;
        return jdbcTemplate.query(sql, new UserMapper(), offset, size);
    }


    /**
     * Retrieves a user entity from the database based on the provided user ID.
     *
     * @param userId the unique identifier of the user to be retrieved
     * @return a {@code UserEntity} object representing the user if found, or {@code null} if no user exists with the given ID
     */
    public UserEntity findById(final long userId) {
        String sql = """
                  SELECT user_id, username, password, email, created_at, updated_at, verified, blocked, token_version
                  FROM users
                  WHERE user_id = ?;
                """;
        try {
            return jdbcTemplate.queryForObject(sql, new UserMapper(), userId);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }


    /**
     * Retrieves a user entity from the database based on the provided email address.
     *
     * @param email the email address of the user to be retrieved
     * @return a {@code UserEntity} object representing the user if found, or {@code null} if no user exists with the given email
     */
    public UserEntity findByEmail(final String email) {
        String sql = """
                  SELECT user_id, username, password, email, created_at, updated_at, verified, blocked, token_version
                  FROM users
                  WHERE email = ?;
                """;
        try {
            return jdbcTemplate.queryForObject(sql, new UserMapper(), email);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }


    /**
     * Checks if a user with the specified user ID is blocked.
     * Retrieves the `blocked` status from the database for the given user.
     *
     * @param userId the unique identifier of the user to check for blocked status
     * @return {@code true} if the user is blocked, {@code false} if not blocked,
     * or {@code null} if no user with the specified ID exists
     */
    public Boolean isBlocked(final long userId) {
        String sql = """
                  SELECT blocked
                  FROM users
                  WHERE user_id = ?;
                """;
        try {
            return jdbcTemplate.queryForObject(sql, Boolean.class, userId);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }


    /**
     * Checks if a user with the specified ID is verified.
     * Retrieves the `verified` status from the database for the given user.
     *
     * @param userId the unique identifier of the user to check for verified status
     * @return {@code true} if the user is verified, {@code false} if not verified,
     * or {@code null} if no user with the specified ID exists
     */
    public Boolean isVerified(final long userId) {
        String sql = """
                  SELECT verified
                  FROM users
                  WHERE user_id = ?;
                """;
        try {
            return jdbcTemplate.queryForObject(sql, Boolean.class, userId);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }


    public int getTokenVersion(final long userId) {
        String sql = """
                  SELECT token_version
                  FROM users
                  WHERE user_id = ?;
                """;
        try {
            var result = jdbcTemplate.queryForObject(sql, Integer.class, userId);
            return result != null ? result : 0;
        } catch (EmptyResultDataAccessException e) {
            return 0;
        }
    }

    public Long save(final UserEntity entity) {
        final String sql = """
                INSERT INTO users (username, password, email)
                VALUES (?, ?, ?);
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        try {
            int updated = jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, entity.getUsername());
                ps.setString(2, entity.getPassword());
                ps.setString(3, entity.getEmail());
                return ps;
            }, keyHolder);

            if (updated == 0) return null;

            Number key = keyHolder.getKey();
            return key != null ? key.longValue() : null;
        } catch (DuplicateKeyException e) {
            return null;
        }
    }

    public int update(final long userId, final UserEntity entity) {
        String sql = """
                  UPDATE users
                  SET username = ?,
                      email = ?,
                      token_version = token_version + 1,
                      updated_at = CURRENT_TIMESTAMP,
                      verified = ?,
                      blocked = ?
                  WHERE user_id = ?;
                """;
        return jdbcTemplate.update(sql,
                entity.getUsername(),
                entity.getEmail(),
                entity.getVerified(),
                entity.getBlocked(),
                userId);
    }


    /**
     * Deletes a user record from the database identified by the specified user ID.
     * Executes a SQL DELETE query to remove the user with the given ID.
     *
     * @param userId the unique identifier of the user to be deleted
     * @return the number of rows affected in the database;
     * typically returns 1 if the delete operation is successful,
     * or 0 if no record with the specified ID exists
     */
    public int deleteById(final long userId) {
        String sql = """ 
                    DELETE FROM users
                    WHERE user_id = ?;
                """;
        return jdbcTemplate.update(sql, userId);
    }

    /**
     * Retrieves a sorted and paginated list of UserEntity objects from the database,
     * filtered by username and email. The method constructs a SQL query based on
     * the provided sorting and filtering parameters.
     *
     * @param offset         the starting point for pagination, indicating the number of records to skip
     * @param size           the maximum number of records to retrieve in the result set
     * @param sortByParam    the column name to sort the results by; must be one of the allowed sort columns
     * @param sortOrderParam the sorting order (e.g., "ASC" or "DESC"); must be one of the allowed sorting orders
     * @param username       the username filter to match records; supports partial matching
     * @param email          the email filter to match records; supports partial matching
     * @return a list of UserEntity objects representing the filtered, sorted, and paginated results
     */
    public List<UserEntity> getSortedPageWithFilters(final int offset, final int size, final String sortByParam, final String sortOrderParam, final String username, final String email) {
        String sql = buildQueryForSortedPage(sortByParam, sortOrderParam);

        String usernameParam = "%" + StringUtils.nullToEmpty(username) + "%";
        String emailParam = "%" + StringUtils.nullToEmpty(email) + "%";

        return jdbcTemplate.query(sql, new UserMapper(), usernameParam, emailParam, offset, size);
    }


    /**
     * Builds a SQL query string for retrieving a sorted and paginated list of roles from the "users" table.
     * Validates the provided sorting parameters before constructing the query.
     *
     * @param sortBy    the column name to sort the results by; must be one of the allowed sort columns
     * @param sortOrder the sorting order (e.g., "ASC" or "DESC"); must be one of the allowed sorting orders
     * @return the constructed SQL query string for retrieving the sorted and paginated roles
     */
    private static String buildQueryForSortedPage(final String sortBy, final String sortOrder) {
        ValidatorUtils.validateSortParameters(sortBy, sortOrder, ALLOWED_SORT_COLUMNS, ALLOWED_SORT_ORDERS);
        String normalizedSortOrder = sortOrder.toUpperCase(Locale.ROOT);
        SortParameters sortParameters = new SortParameters(sortBy, normalizedSortOrder);
        return String.format("""
                    SELECT user_id, username, password, email
                    FROM users
                    WHERE username LIKE ?
                    AND email LIKE ?
                    ORDER BY %s %s
                    LIMIT ?, ?;
                """.formatted(sortParameters.sortBy(), sortParameters.sortOrder()));
    }


    /**
     * Retrieves the count of user records from the database that match the specified
     * username and email filters. The method performs a case-insensitive search
     * using SQL LIKE operators on both the username and email fields.
     *
     * @param username the username filter to match records; supports partial matching
     * @param email    the email filter to match records; supports partial matching
     * @return the count of records that match the specified filters
     */
    public Long getFiltersCount(final String username, final String email) {
        String sql = """ 
                SELECT COUNT(*)
                FROM users
                WHERE username LIKE ?
                AND email LIKE ?
                """;
        String usernameParam = "%" + StringUtils.nullToEmpty(username) + "%";
        String emailParam = "%" + StringUtils.nullToEmpty(email) + "%";
        return jdbcTemplate.queryForObject(sql, Long.class, usernameParam, emailParam);
    }

    /**
     * Checks if the provided email exists in the database by querying the users table.
     *
     * @param email the email address to be checked for existence
     * @return true if the email exists, false otherwise
     */
    public boolean isEmailExist(final String email) {
        String sql = """
                SELECT COUNT(*)
                FROM users
                WHERE email = ?
                """;
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, email);
        return count != null && count > 0;
    }

    /**
     * Checks if a given login exists in the database by querying the users table.
     *
     * @param login the username to be checked for existence in the database
     * @return true if the login exists, false otherwise
     */
    public boolean isLoginExist(final String login) {
        String sql = """
                SELECT COUNT(*)
                FROM users
                WHERE username = ?
                """;
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, login);
        return count != null && count > 0;
    }

    public boolean isValidId(final Long id) {
        String sql = """
                SELECT COUNT(*)
                FROM users
                WHERE users.user_id = ?
                """;
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }


    /**
     * Unlocks a user by updating their status in the database.
     * This method sets the `verified` column to true, `blocked` column to false,
     * and updates the `updated_at` timestamp to the current time.
     *
     * @param userId the unique identifier of the user to be unlocked
     */
    public void unlock(final long userId) {
        String sql = """
                UPDATE users
                SET verified = true,
                    blocked = false,
                    token_version = token_version + 1,
                    updated_at = CURRENT_TIMESTAMP
                WHERE user_id = ?;
                """;
        jdbcTemplate.update(sql, userId);
    }

    /**
     * Updates the status of a user in the database identified by the specified userId.
     * This method updates the `verified` and `blocked` columns for the user and
     * sets the `updated_at` timestamp to the current time.
     *
     * @param userId   the unique identifier of the user whose status is to be updated
     * @param verified the new verified status to be set for the user
     * @param blocked  the new blocked status to be set for the user
     */
    public void updateStatus(final long userId, final boolean verified, final boolean blocked) {
        String sql = """
                UPDATE users
                SET verified = ?,
                    blocked = ?,
                    token_version = token_version + 1,
                    updated_at = CURRENT_TIMESTAMP
                WHERE user_id = ?;
                """;
        jdbcTemplate.update(sql, verified, blocked, userId);
    }


    public int updatePassword(final long userId, final String encryptedPassword) {
        String sql = """
                UPDATE users
                SET password = ?,
                    token_version = token_version + 1,
                    updated_at = CURRENT_TIMESTAMP
                WHERE user_id = ?;
                """;
        return jdbcTemplate.update(sql, encryptedPassword, userId);
    }

    public int incrementTokenVersion(final Long userId) {
        final String sql = """
                UPDATE users
                SET token_version = token_version + 1,
                    updated_at = CURRENT_TIMESTAMP
                WHERE user_id = ?;
                """;
        return jdbcTemplate.update(sql, userId);
    }

    /**
     * The UserMapper class is a private static implementation of the RowMapper interface.
     * It is designed to map rows of a ResultSet to instances of the UserEntity class.
     * <p>
     * This class is responsible for reading the fields `user_id`, `username`, `email`,
     * and `password` from a ResultSet and constructing a UserEntity object corresponding
     * to the data in each row.
     * <p>
     * It is used internally in database query operations to translate database records
     * into UserEntity objects.
     */
    static class UserMapper implements RowMapper<UserEntity> {
        @Override
        public UserEntity mapRow(ResultSet resultSet, int i) throws SQLException {

            long userId = resultSet.getLong("user_id");
            String username = resultSet.getString("username");
            String email = resultSet.getString("email");
            String password = resultSet.getString("password");
            java.sql.Timestamp createdAt = resultSet.getTimestamp("created_at");
            java.sql.Timestamp updatedAt = resultSet.getTimestamp("updated_at");
            Boolean verified = resultSet.getBoolean("verified");
            Boolean blocked = resultSet.getBoolean("blocked");
            int tokenVersion = resultSet.getInt("token_version");

            return new UserEntity(userId, username, email, password, createdAt, updatedAt, verified, blocked, tokenVersion);
        }
    }

}
