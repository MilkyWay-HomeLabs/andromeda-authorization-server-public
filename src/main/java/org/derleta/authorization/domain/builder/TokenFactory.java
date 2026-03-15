package org.derleta.authorization.domain.builder;

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
    public static TokenEntity createToken(TokenType type, long tokenId, long userId, String token, Timestamp expirationDate, int version, String jti, boolean revoked) {
        return switch (type) {
            case TokenType.ACCESS ->
                    new AccessTokenEntity(tokenId, userId, token, expirationDate, version, jti, revoked);
            case TokenType.REFRESH ->
                    new RefreshTokenEntity(tokenId, userId, token, expirationDate, version, jti, revoked);
            case TokenType.CONFIRMATION ->
                    new ConfirmationTokenEntity(tokenId, userId, token, expirationDate, version, jti, revoked);
            default -> throw new IllegalArgumentException("Unknown token type: " + type);
        };
    }

}
