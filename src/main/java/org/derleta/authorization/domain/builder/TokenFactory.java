package org.derleta.authorization.domain.builder;

import org.derleta.authorization.domain.entity.UserEntity;
import org.derleta.authorization.domain.entity.token.AccessTokenEntity;
import org.derleta.authorization.domain.entity.token.ConfirmationTokenEntity;
import org.derleta.authorization.domain.entity.token.RefreshTokenEntity;
import org.derleta.authorization.domain.entity.token.TokenEntity;
import org.derleta.authorization.domain.types.TokenType;

import java.sql.Timestamp;

/**
 * A factory class for creating instances of various token entity types, such as
 * AccessTokenEntity, RefreshTokenEntity, and ConfirmationTokenEntity. This class
 * provides a single static method to create a specific type of token entity based
 * on provided inputs.
 */
public class TokenFactory {
    public static TokenEntity createToken(TokenType type, long tokenId, UserEntity user, String token, Timestamp expirationDate) {
        return switch (type) {
            case TokenType.ACCESS -> new AccessTokenEntity(tokenId, user, token, expirationDate);
            case TokenType.REFRESH -> new RefreshTokenEntity(tokenId, user, token, expirationDate);
            case TokenType.CONFIRMATION -> new ConfirmationTokenEntity(tokenId, user, token, expirationDate);
            default -> throw new IllegalArgumentException("Unknown token type: " + type);
        };
    }
}
