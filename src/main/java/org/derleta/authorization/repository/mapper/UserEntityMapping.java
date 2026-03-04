package org.derleta.authorization.repository.mapper;

import org.derleta.authorization.domain.entity.UserEntity;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * Shared helper for mapping a {@link UserEntity} from a {@link ResultSet}.
 *
 * <p>We use it in mappers that perform a JOIN on the {@code users} table and return columns with aliases
 * like {@code u.user_id}, {@code u.username}, etc.
 *
 * <p>If a given query does not use a table prefix/alias, pass an empty prefix ({@code ""}).
 */
public final class UserEntityMapping {

    private UserEntityMapping() {
    }

    /**
     * Maps a user from columns named in the format: {@code <prefix>user_id}, {@code <prefix>username}, ...
     *
     * @param rs     query result
     * @param prefix column prefix, e.g. {@code "u."} or {@code ""}
     */
    public static UserEntity mapUser(ResultSet rs, String prefix) throws SQLException {
        String p = prefix == null ? "" : prefix;

        long userId = rs.getLong(p + "user_id");
        String username = rs.getString(p + "username");
        String email = rs.getString(p + "email");
        String password = rs.getString(p + "password");
        Timestamp createdAt = rs.getTimestamp(p + "created_at");
        Timestamp updatedAt = rs.getTimestamp(p + "updated_at");
        boolean verified = rs.getBoolean(p + "verified");
        boolean blocked = rs.getBoolean(p + "blocked");
        int tokenVersion = rs.getInt(p + "token_version");

        return new UserEntity(userId, username, email, password, createdAt, updatedAt, verified, blocked, tokenVersion);
    }
}
