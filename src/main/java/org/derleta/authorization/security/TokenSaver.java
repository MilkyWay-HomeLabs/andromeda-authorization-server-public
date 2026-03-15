package org.derleta.authorization.security;

import java.sql.Timestamp;

@FunctionalInterface
public interface TokenSaver {
    int save(long userId, String encryptedToken, Timestamp expirationTs, int version, String jti, boolean revoked);
}
