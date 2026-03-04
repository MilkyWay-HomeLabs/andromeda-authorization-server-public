package org.derleta.authorization.repository.impl;

import org.derleta.authorization.domain.entity.UserRoleEntity;
import org.derleta.authorization.repository.RepositoryClass;
import org.derleta.authorization.repository.mapper.UserRoleMapper;
import org.derleta.authorization.repository.sort.SortParameters;
import org.derleta.authorization.utils.StringUtils;
import org.derleta.authorization.utils.ValidatorUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Repository class responsible for managing database operations related
 * to user-role mappings in the "andromeda" schema. This includes fetching,
 * saving, deleting, and filtering user-role entries, as well as retrieving
 * related user and role details.
 * <p>
 * The class uses Spring's {@link JdbcTemplate} to perform SQL queries and updates.
 * It operates on the "user_roles" table and integrates with the "users" and "roles"
 * tables to fetch complete user-role information.
 */
@Repository
public class UserRoleRepository implements RepositoryClass {

    private static final Set<String> ALLOWED_SORT_COLUMNS = Set.of("u.user_id", "u.username", "u.email", "r.role_id", "r.role_name");
    private static final Set<String> ALLOWED_SORT_ORDERS = Set.of("ASC", "DESC");

    private final JdbcTemplate jdbcTemplate;

    /**
     * Constructs an instance of UserRoleRepository and initializes the JdbcTemplate
     * with the provided DataSource for database operations.
     *
     * @param dataSource the DataSource to be used for initializing the JdbcTemplate
     */
    @Autowired
    public UserRoleRepository(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /**
     * Retrieves the total count of user-role mappings from the database.
     *
     * @return the total number of user-role mappings as an Integer
     */
    public Integer getSize() {
        String sql = """
                SELECT COUNT(*) FROM user_roles;
                """;
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

    /**
     * Retrieves a paginated list of user-role mappings from the database.
     * This method fetches user-role records along with associated user and role details
     * and maps the result to a list of {@link UserRoleEntity} objects.
     *
     * @param offset the starting position for the records to retrieve
     * @param size   the number of records to retrieve
     * @return a list of {@link UserRoleEntity} objects containing user, role, and user-role details
     */
    public List<UserRoleEntity> getPage(final int offset, final int size) {
        String sql = """ 
                SELECT ur.user_role_id, u.*, r.*
                FROM users u
                JOIN user_roles ur ON u.user_id = ur.user_id
                JOIN roles r ON ur.role_id = r.role_id
                LIMIT ?, ?;
                """;
        return jdbcTemplate.query(sql, new UserRoleMapper(), offset, size);
    }

    public UserRoleEntity findByIds(final long userId, final int roleId) {
        String sql = """
                  SELECT ur.user_role_id, u.*, r.*
                  FROM users u
                  JOIN user_roles ur ON u.user_id = ur.user_id
                  JOIN roles r ON ur.role_id = r.role_id
                  WHERE u.user_id = ?
                  AND r.role_id = ?;
                """;
        try {
            return jdbcTemplate.queryForObject(sql, new UserRoleMapper(), userId, roleId);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public UserRoleEntity findById(final long userRoleId) {
        String sql = """
                  SELECT ur.user_role_id, u.*, r.*
                  FROM users u
                  JOIN user_roles ur ON u.user_id = ur.user_id
                  JOIN roles r ON ur.role_id = r.role_id
                  WHERE user_role_id = ?;
                """;
        try {
            return jdbcTemplate.queryForObject(sql, new UserRoleMapper(), userRoleId);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public Long save(final long userId, final int roleId) {
        String sql = """
                INSERT INTO user_roles (user_id, role_id)
                VALUES (?, ?);
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        try {
            int updated = jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setLong(1, userId);
                ps.setInt(2, roleId);
                return ps;
            }, keyHolder);

            if (updated == 0) return null;

            Number key = keyHolder.getKey();
            return key != null ? key.longValue() : null;
        } catch (DuplicateKeyException e) {
            return null;
        }
    }

    public int deleteById(final long userId, final int roleId) {
        String sql = """
                DELETE FROM user_roles
                WHERE user_id = ? AND role_id = ?;
                """;
        return jdbcTemplate.update(sql, userId, roleId);
    }

    /**
     * Retrieves a sorted and paginated list of user-role mappings based on specified filter conditions.
     * This method filters by username, email, and role name, and sorts the results based on the given
     * sorting parameters.
     *
     * @param offset         the starting position for the records to retrieve
     * @param size           the number of records to retrieve
     * @param sortByParam    the column name to sort the results by
     * @param sortOrderParam the sorting order, either "ASC" for ascending or "DESC" for descending
     * @param username       the filter criteria for the username; supports partial matching using wildcards
     * @param email          the filter criteria for the email; supports partial matching using wildcards
     * @param roleName       the filter criteria for the role name; supports partial matching using wildcards
     * @return a list of {@link UserRoleEntity} objects containing user, role, and user-role details
     */
    public List<UserRoleEntity> getSortedPageWithFilters(final int offset, final int size, final String sortByParam, final String sortOrderParam, final String username, final String email, final String roleName) {
        String sql = buildQueryForSortedList(sortByParam, sortOrderParam);
        String usernameParam = "%" + username + "%";
        String emailParam = "%" + email + "%";
        String roleNameParam = "%" + roleName + "%";
        return jdbcTemplate.query(sql, new UserRoleMapper(),
                usernameParam, emailParam, roleNameParam,
                offset, size);
    }

    /**
     * Builds a SQL query string for retrieving a sorted list of user-role mappings based on
     * specified sorting criteria. Validates the provided sort parameters before constructing
     * the query to ensure they match the allowed sorting options.
     *
     * @param sortBy    the column name to sort the results by
     * @param sortOrder the sorting order, either "ASC" for ascending or "DESC" for descending
     * @return a formatted SQL query string with the specified sorting conditions
     */
    private static String buildQueryForSortedList(final String sortBy, final String sortOrder) {
        ValidatorUtils.validateSortParameters(sortBy, sortOrder, ALLOWED_SORT_COLUMNS, ALLOWED_SORT_ORDERS);
        String normalizedSortOrder = sortOrder.toUpperCase(Locale.ROOT);
        SortParameters sortParameters = new SortParameters(sortBy, normalizedSortOrder);
        return String.format("""
                    SELECT ur.user_role_id, u.*, r.*
                    FROM users u
                    JOIN user_roles ur ON u.user_id = ur.user_id
                    JOIN roles r ON ur.role_id = r.role_id
                    WHERE u.username LIKE ?
                    AND u.email LIKE ?
                    AND r.role_name LIKE ?
                    ORDER BY %s %s
                    LIMIT ?, ?;
                """.formatted(sortParameters.sortBy(), sortParameters.sortOrder()));
    }

    public Long getFiltersCount(final String username, final String email, final String roleName) {
        String sql = """
                SELECT COUNT(*)
                FROM users u
                JOIN user_roles ur ON u.user_id = ur.user_id
                JOIN roles r ON ur.role_id = r.role_id
                WHERE u.username LIKE ?
                AND u.email LIKE ?
                AND r.role_name LIKE ?
                """;

        String usernameParam = "%" + StringUtils.nullToEmpty(username) + "%";
        String emailParam = "%" + StringUtils.nullToEmpty(email) + "%";
        String roleNameParam = "%" + StringUtils.nullToEmpty(roleName) + "%";

        return jdbcTemplate.queryForObject(sql, Long.class, usernameParam, emailParam, roleNameParam);
    }
}
