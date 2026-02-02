package org.derleta.authorization.domain.entity.token;

import org.derleta.authorization.domain.entity.UserEntity;

import java.sql.Timestamp;

public class RefreshTokenEntity extends TokenEntity {

    public RefreshTokenEntity() {
    }

    public RefreshTokenEntity(long tokenId, UserEntity user, String token, Timestamp expirationDate) {
        super(tokenId, user, token, expirationDate);
    }

}
