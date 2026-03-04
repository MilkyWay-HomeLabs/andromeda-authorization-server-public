package org.derleta.authorization.domain.builder.impl;

import org.derleta.authorization.domain.builder.ConfirmationTokenBuilder;
import org.derleta.authorization.domain.model.ConfirmationToken;

import java.sql.Timestamp;

/**
 * Implementation of the {@link ConfirmationTokenBuilder} interface.
 * This class provides a concrete builder for constructing instances of {@link ConfirmationToken}.
 * It allows setting various properties of the confirmation token and constructing a fully initialized object.
 */
public class ConfirmationTokenBuilderImpl implements ConfirmationTokenBuilder {

    private long tokenId;
    private long userId;
    private String token;
    private Timestamp expirationDate;
    private int version;
    private String jti;
    private boolean revoked;

    @Override
    public ConfirmationTokenBuilder tokenId(long tokenId) {
        this.tokenId = tokenId;
        return this;
    }

    @Override
    public ConfirmationTokenBuilder userId(long userId) {
        this.userId = userId;
        return this;
    }

    @Override
    public ConfirmationTokenBuilder token(String token) {
        this.token = token;
        return this;
    }

    @Override
    public ConfirmationTokenBuilder expirationDate(Timestamp expirationDate) {
        this.expirationDate = expirationDate;
        return this;
    }

    @Override
    public ConfirmationTokenBuilder version(int version) {
        this.version = version;
        return this;
    }

    @Override
    public ConfirmationTokenBuilder jti(String jti) {
        this.jti = jti;
        return this;
    }

    @Override
    public ConfirmationTokenBuilder revoked(boolean revoked) {
        this.revoked = revoked;
        return this;
    }

    @Override
    public ConfirmationToken build() {
        return new ConfirmationToken(tokenId, token, userId, expirationDate, version, jti, revoked);
    }

}
