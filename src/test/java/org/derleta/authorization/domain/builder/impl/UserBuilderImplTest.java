package org.derleta.authorization.domain.builder.impl;

import org.derleta.authorization.domain.model.User;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

class UserBuilderImplTest {

    @Test
    void testBuild() {
        long userId = 1L;
        String username = "testuser";
        String password = "password";
        String email = "test@example.com";
        Timestamp createdAt = new Timestamp(System.currentTimeMillis());
        Timestamp updatedAt = new Timestamp(System.currentTimeMillis());
        Boolean verified = true;
        Boolean blocked = false;
        int tokenVersion = 1;

        UserBuilderImpl builder = new UserBuilderImpl();
        User user = builder
                .userId(userId)
                .username(username)
                .email(email)
                .password(password)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .verified(verified)
                .blocked(blocked)
                .tokenVersion(tokenVersion)
                .build();

        assertNotNull(user);
        assertEquals(userId, user.userId());
        assertEquals(username, user.username());
        assertEquals(email, user.email());
        assertEquals(password, user.password());
        assertEquals(createdAt, user.createdAt());
        assertEquals(updatedAt, user.updatedAt());
        assertEquals(verified, user.verified());
        assertEquals(blocked, user.blocked());
        assertEquals(tokenVersion, user.tokenVersion());
    }
}
