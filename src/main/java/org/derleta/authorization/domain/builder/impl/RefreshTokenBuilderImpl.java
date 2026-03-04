package org.derleta.authorization.domain.builder.impl;

import org.derleta.authorization.domain.builder.RefreshTokenBuilder;
import org.derleta.authorization.domain.model.RefreshToken;

import java.sql.Timestamp;


/**
 * Implementation of the {@link RefreshTokenBuilder} interface.
 * This class provides a concrete implementation for constructing {@link RefreshToken} objects.
 * It supports the fluent builder pattern, enabling the step-by-step creation of a {@link RefreshToken} instance
 * by setting its properties, such as the token ID, user, token, and expiration date.
 * <p>
 * The class ensures that a fully initialized {@link RefreshToken} instance can be created by chaining method calls
 * to set desired properties before invoking the build method.
 */
public class RefreshTokenBuilderImpl implements RefreshTokenBuilder {


    private long tokenId;
    private long userId;
    private String token;
    private Timestamp expirationDate;
    private int version;
    private String jti;
    private boolean revoked;

    @Override
    public RefreshTokenBuilder tokenId(long tokenId) {
        this.tokenId = tokenId;
        return this;
    }

    @Override
    public RefreshTokenBuilder userId(long userId) {
        this.userId = userId;
        return this;
    }

    @Override
    public RefreshTokenBuilder token(String token) {
        this.token = token;
        return this;
    }

    @Override
    public RefreshTokenBuilder expirationDate(Timestamp expirationDate) {
        this.expirationDate = expirationDate;
        return this;
    }

    @Override
    public RefreshTokenBuilder version(int version) {
        this.version = version;
        return this;
    }

    @Override
    public RefreshTokenBuilder jti(String jti) {
        this.jti = jti;
        return this;
    }

    @Override
    public RefreshTokenBuilder revoked(boolean revoked) {
        this.revoked = revoked;
        return this;
    }

    @Override
    public RefreshToken build() {
        return new RefreshToken(tokenId, token, userId, expirationDate, version, jti, revoked);
    }

}
