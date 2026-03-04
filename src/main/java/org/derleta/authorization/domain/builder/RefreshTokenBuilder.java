package org.derleta.authorization.domain.builder;

import org.derleta.authorization.domain.model.RefreshToken;

import java.sql.Timestamp;

/**
 * A builder interface for constructing instances of {@link RefreshToken}.
 * This interface provides methods to set the properties of a JWT token, enabling
 * the creation of a fully initialized {@link RefreshToken} object through method chaining.
 */
public interface RefreshTokenBuilder {

    RefreshTokenBuilder tokenId(long tokenId);

    RefreshTokenBuilder userId(long userId);

    RefreshTokenBuilder token(String token);

    RefreshTokenBuilder expirationDate(Timestamp expirationDate);

    RefreshTokenBuilder version(int version);

    RefreshTokenBuilder jti(String jti);

    RefreshTokenBuilder revoked(boolean revoked);

    RefreshToken build();

}
