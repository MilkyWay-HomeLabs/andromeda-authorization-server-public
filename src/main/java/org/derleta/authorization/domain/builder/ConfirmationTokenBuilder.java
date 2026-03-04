package org.derleta.authorization.domain.builder;

import org.derleta.authorization.domain.model.ConfirmationToken;

import java.sql.Timestamp;

/**
 * A builder interface for constructing instances of {@link ConfirmationToken}.
 * This interface provides methods to set properties of a confirmation token and
 * allows chaining of calls to build a fully initialized {@link ConfirmationToken} object.
 */
public interface ConfirmationTokenBuilder {

    ConfirmationTokenBuilder tokenId(long tokenId);

    ConfirmationTokenBuilder userId(long userId);

    ConfirmationTokenBuilder token(String token);

    ConfirmationTokenBuilder expirationDate(Timestamp expirationDate);

    ConfirmationTokenBuilder version(int version);

    ConfirmationTokenBuilder jti(String jti);

    ConfirmationTokenBuilder revoked(boolean revoked);

    ConfirmationToken build();

}
