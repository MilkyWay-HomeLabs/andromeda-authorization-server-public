package org.derleta.authorization.controller.mapper;

import org.derleta.authorization.controller.dto.request.impl.UserRegistrationRequest;
import org.derleta.authorization.controller.dto.request.impl.UserRequest;
import org.derleta.authorization.controller.dto.response.UserResponse;
import org.derleta.authorization.domain.entity.UserEntity;
import org.derleta.authorization.domain.model.User;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserApiMapperTest {

    @Test
    void testToUser_FromEntity() {
        Timestamp now = Timestamp.valueOf(LocalDateTime.now());
        UserEntity entity = new UserEntity(1L, "user", "email@test.com", "pass", now, now, true, false, 0);
        
        User user = UserApiMapper.toUser(entity);
        
        assertNotNull(user);
        assertEquals(1L, user.userId());
        assertEquals("user", user.username());
        assertEquals("email@test.com", user.email());
        assertEquals("pass", user.password());
        assertEquals(now, user.createdAt());
        assertEquals(now, user.updatedAt());
        assertTrue(user.verified());
        assertFalse(user.blocked());
        assertEquals(0, user.tokenVersion());
    }

    @Test
    void testToUser_FromEntity_Null() {
        assertNull(UserApiMapper.toUser(null));
    }

    @Test
    void testToUsers() {
        UserEntity entity = new UserEntity();
        entity.setUserId(1L);
        
        List<User> users = UserApiMapper.toUsers(List.of(entity));
        
        assertEquals(1, users.size());
        assertEquals(1L, users.get(0).userId());
    }

    @Test
    void testToUsers_Null() {
        assertTrue(UserApiMapper.toUsers(null).isEmpty());
    }

    @Test
    void testToUserResponse() {
        User user = new org.derleta.authorization.domain.builder.impl.UserBuilderImpl()
                .userId(1L)
                .username("user")
                .email("email@test.com")
                .build();
        
        UserResponse response = UserApiMapper.toUserResponse(user);
        
        assertNotNull(response);
        assertEquals(1L, response.getUserId());
        assertEquals("user", response.getUsername());
        assertEquals("email@test.com", response.getEmail());
    }

    @Test
    void testToUserResponse_Null() {
        assertNull(UserApiMapper.toUserResponse(null));
    }

    @Test
    void testToUser_FromRegistrationRequest() {
        UserRegistrationRequest request = new UserRegistrationRequest("user", "pass", "email@test.com");
        
        User user = UserApiMapper.toUser(1L, request);
        
        assertNotNull(user);
        assertEquals(1L, user.userId());
        assertEquals("user", user.username());
        assertEquals("pass", user.password());
        assertEquals("email@test.com", user.email());
    }

    @Test
    void testToUser_FromRegistrationRequest_Null() {
        assertNull(UserApiMapper.toUser(1L, null));
    }

    @Test
    void testToUserFromRequest() {
        UserRequest request = new UserRequest("user", "pass", "email@test.com");
        
        User user = UserApiMapper.toUserFromRequest(request);
        
        assertNotNull(user);
        assertEquals("user", user.username());
        assertEquals("pass", user.password());
        assertEquals("email@test.com", user.email());
    }

    @Test
    void testToUserFromRequest_Null() {
        assertNull(UserApiMapper.toUserFromRequest(null));
    }

    @Test
    void testToUserPatch() {
        UserRequest request = new UserRequest("user", null, "email@test.com");
        
        User user = UserApiMapper.toUserPatch(request);
        
        assertNotNull(user);
        assertEquals("user", user.username());
        assertEquals("email@test.com", user.email());
        assertNull(user.password());
    }

    @Test
    void testToUserPatch_Null() {
        assertNull(UserApiMapper.toUserPatch(null));
    }

    @Test
    void testToUserEntity() {
        UserRegistrationRequest request = new UserRegistrationRequest("user", "pass", "email@test.com");
        
        UserEntity entity = UserApiMapper.toUserEntity(request);
        
        assertNotNull(entity);
        assertEquals("user", entity.getUsername());
        assertEquals("pass", entity.getPassword());
        assertEquals("email@test.com", entity.getEmail());
    }
}
