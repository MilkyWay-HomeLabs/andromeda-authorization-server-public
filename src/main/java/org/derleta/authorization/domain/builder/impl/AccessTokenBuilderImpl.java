package org.derleta.authorization.domain.builder.impl;

import org.derleta.authorization.domain.builder.AccessTokenBuilder;
import org.derleta.authorization.domain.model.AccessToken;

import java.sql.Timestamp;

/**
 * Implementation of the {@link AccessTokenBuilder} interface.
 * This class provides a concrete implementation for building {@link AccessToken} objects.
 * It supports the fluent builder pattern, enabling the step-by-step creation of a {@link AccessToken} instance
 * by setting its properties such as the token ID, user, token, and expiration date.
 * <p>
 * This builder implementation ensures that a fully initialized instance of {@link AccessToken} can be created
 * by chaining method calls to set desired properties before invoking the build method.
 */
public class AccessTokenBuilderImpl implements AccessTokenBuilder {

    private long tokenId;
    private long userId;
    private String token;
    private Timestamp expirationDate;
    private int version;
    private String jti;
    private boolean revoked;

    @Override
    public AccessTokenBuilder tokenId(long tokenId) {
        this.tokenId = tokenId;
        return this;
    }

    @Override
    public AccessTokenBuilder userId(long userId) {
        this.userId = userId;
        return this;
    }

    @Override
    public AccessTokenBuilder token(String token) {
        this.token = token;
        return this;
    }

    @Override
    public AccessTokenBuilder expirationDate(Timestamp expirationDate) {
        this.expirationDate = expirationDate;
        return this;
    }

    @Override
    public AccessTokenBuilder version(int version) {
        this.version = version;
        return this;
    }

    @Override
    public AccessTokenBuilder jti(String jti) {
        this.jti = jti;
        return this;
    }

    @Override
    public AccessTokenBuilder revoked(boolean revoked) {
        this.revoked = revoked;
        return this;
    }

    @Override
    public AccessToken build() {
        return new AccessToken(tokenId, token, userId, expirationDate, version, jti, revoked);
    }

}
