package org.derleta.authorization.domain.builder;

import org.derleta.authorization.domain.model.AccessToken;
import org.derleta.authorization.domain.model.User;

import java.sql.Timestamp;

/**
 * A builder interface for constructing instances of {@link AccessToken}.
 * This interface provides methods to set the properties of a JWT token, enabling
 * the creation of a fully initialized {@link AccessToken} object through method chaining.
 */
public interface AccessTokenBuilder {

    AccessTokenBuilder tokenId(long tokenId);

    AccessTokenBuilder user(User user);

    AccessTokenBuilder token(String token);

    AccessTokenBuilder expirationDate(Timestamp expirationDate);

    AccessToken build();

}
