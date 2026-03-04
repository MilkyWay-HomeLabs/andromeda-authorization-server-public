package org.derleta.authorization.repository.impl;

import org.derleta.authorization.domain.entity.RoleEntity;
import org.derleta.authorization.domain.entity.UserEntity;
import org.derleta.authorization.domain.entity.UserRoleEntity;
import org.derleta.authorization.repository.RepositoryClass;
import org.derleta.authorization.repository.mapper.UserEntityMapping;
import org.derleta.authorization.security.mapper.UserSecurityMapper;
import org.derleta.authorization.security.model.UserSecurity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Repository class that facilitates database operations for token generation
 * and user security details retrieval. It uses JdbcTemplate to execute SQL
 * queries and maps database result sets to the application's user and role
 * entities.
 */
@Repository
public class TokensGeneratorRepository implements RepositoryClass {

    private final JdbcTemplate jdbcTemplate;

    /**
     * Constructs a new instance of TokensGeneratorRepository and initializes it
     * with a JdbcTemplate object using the provided DataSource.
     *
     * @param dataSource the DataSource to be used for database interactions
     */
    @Autowired
    public TokensGeneratorRepository(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public Optional<UserSecurity> findByLogin(final String login) {
        final String sql = """
                SELECT ur.user_id,
                       u.user_id   AS u_user_id,
                       u.username  AS u_username,
                       u.password  AS u_password,
                       u.email     AS u_email,
                       u.created_at AS u_created_at,
                       u.updated_at AS u_updated_at,
                       u.verified  AS u_verified,
                       u.blocked   AS u_blocked,
                       u.token_version AS u_token_version,
                       r.role_id,
                       r.role_name
                FROM users u
                JOIN user_roles ur ON u.user_id = ur.user_id
                JOIN roles r ON ur.role_id = r.role_id
                WHERE u.username = ?;
                """;
        List<UserRoleEntity> rows = jdbcTemplate.query(sql, new UserRolesSecurityMapper(), login);
        return getUserWithRoles(rows);
    }

    public Optional<UserSecurity> findByEmail(final String email) {
        final String sql = """
                SELECT ur.user_id,
                       u.user_id   AS u_user_id,
                       u.username  AS u_username,
                       u.password  AS u_password,
                       u.email     AS u_email,
                       u.created_at AS u_created_at,
                       u.updated_at AS u_updated_at,
                       u.verified  AS u_verified,
                       u.blocked   AS u_blocked,
                       u.token_version AS u_token_version,
                       r.role_id,
                       r.role_name
                FROM users u
                JOIN user_roles ur ON u.user_id = ur.user_id
                JOIN roles r ON ur.role_id = r.role_id
                WHERE u.email = ?;
                """;
        List<UserRoleEntity> rows = jdbcTemplate.query(sql, new UserRolesSecurityMapper(), email);
        return getUserWithRoles(rows);
    }


    private Optional<UserSecurity> getUserWithRoles(final List<UserRoleEntity> usersRolesEntityList) {
        if (usersRolesEntityList == null || usersRolesEntityList.isEmpty()) {
            return Optional.empty();
        }

        UserEntity user = usersRolesEntityList.getFirst().getUserEntity();
        if (user == null) {
            return Optional.empty();
        }

        Set<RoleEntity> roles = usersRolesEntityList.stream()
                .map(UserRoleEntity::getRoleEntity)
                .collect(Collectors.toSet());

        return Optional.of(UserSecurityMapper.toUserSecurity(user, roles));
    }


    private static class UserRolesSecurityMapper implements RowMapper<UserRoleEntity> {
        @Override
        public UserRoleEntity mapRow(ResultSet resultSet, int rowNum) throws SQLException {
            long userId = resultSet.getLong("user_id");

            UserEntity userEntity = UserEntityMapping.mapUser(resultSet, "u_");

            int roleId = resultSet.getInt("role_id");
            String roleName = resultSet.getString("role_name");
            RoleEntity roleEntity = new RoleEntity(roleId, roleName);

            return new UserRoleEntity(userId, userEntity, roleEntity);
        }
    }

}
