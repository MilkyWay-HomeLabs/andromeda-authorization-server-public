package org.derleta.authorization.domain.builder.impl;

import org.derleta.authorization.domain.builder.UserBuilder;
import org.derleta.authorization.domain.model.User;

import java.sql.Timestamp;

/**
 * Implementation of the {@link UserBuilder} interface.
 * This class provides a concrete implementation for building {@link User} objects.
 * It supports the fluent builder pattern, allowing for the step-by-step creation
 * of a {@link User} instance by setting its properties.
 */
public class UserBuilderImpl implements UserBuilder {

    private long userId;
    private String username;
    private String password;
    private String email;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private Boolean verified;
    private Boolean blocked;
    private int tokenVersion;

    @Override
    public UserBuilder userId(long userId) {
        this.userId = userId;
        return this;
    }

    @Override
    public UserBuilder username(String username) {
        this.username = username;
        return this;
    }

    @Override
    public UserBuilder email(String email) {
        this.email = email;
        return this;
    }

    @Override
    public UserBuilder password(String password) {
        this.password = password;
        return this;
    }

    @Override
    public UserBuilder createdAt(Timestamp createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    @Override
    public UserBuilder updatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    @Override
    public UserBuilder verified(Boolean verified) {
        this.verified = verified;
        return this;
    }

    @Override
    public UserBuilder blocked(Boolean blocked) {
        this.blocked = blocked;
        return this;
    }

    @Override
    public UserBuilder tokenVersion(int tokenVersion) {
        this.tokenVersion = tokenVersion;
        return this;
    }

    @Override
    public User build() {
        return new User(userId, username, password, email, createdAt, updatedAt, verified, blocked, tokenVersion);
    }

}
