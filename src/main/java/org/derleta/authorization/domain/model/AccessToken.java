package org.derleta.authorization.domain.model;

import java.sql.Timestamp;

public record AccessToken(
        long tokenId,
        String token,
        long userId,
        Timestamp expirationDate,
        int version,
        String jti,
        boolean revoked)
implements Token {

}
