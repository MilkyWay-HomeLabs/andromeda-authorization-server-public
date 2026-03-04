package org.derleta.authorization.domain.builder;

import org.derleta.authorization.domain.model.User;

import java.sql.Timestamp;

/**
 * A builder interface for creating instances of {@link User}.
 * This interface provides methods to set the properties of a User object, enabling
 * the creation of a fully initialized {@link User} instance. The methods support
 * method chaining for simplified and fluent construction.
 */
public interface UserBuilder {

    UserBuilder userId(long userId);

    UserBuilder username(String username);

    UserBuilder email(String email);

    UserBuilder password(String password);

    UserBuilder createdAt(Timestamp createdDate);

    UserBuilder updatedAt(Timestamp updatedAt);

    UserBuilder verified(Boolean enabled);

    UserBuilder blocked(Boolean blocked);

    UserBuilder tokenVersion(int tokenVersion);

    User build();

}
