package org.derleta.authorization.domain.entity.token;

import java.sql.Timestamp;

public class RefreshTokenEntity extends TokenEntity {

    public RefreshTokenEntity() {
    }

    public RefreshTokenEntity(long tokenId, long userId, String token, Timestamp expirationDate, int version, String jti, boolean revoked) {
        super(tokenId, userId, token, expirationDate, version, jti, revoked);
    }

}
