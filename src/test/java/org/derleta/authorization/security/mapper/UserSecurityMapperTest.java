package org.derleta.authorization.security.mapper;

import org.derleta.authorization.domain.entity.RoleEntity;
import org.derleta.authorization.domain.entity.UserEntity;
import org.derleta.authorization.security.model.RoleSecurity;
import org.derleta.authorization.security.model.UserSecurity;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserSecurityMapperTest {

    @Test
    void testToRoleSecurity() {
        RoleEntity roleEntity = new RoleEntity(1, "ROLE_USER");
        RoleSecurity roleSecurity = UserSecurityMapper.toRoleSecurity(roleEntity);

        assertNotNull(roleSecurity);
        assertEquals(1, roleSecurity.getId());
        assertEquals("ROLE_USER", roleSecurity.getName());
    }

    @Test
    void testToRolesSecurity() {
        RoleEntity role1 = new RoleEntity(1, "ROLE_USER");
        RoleEntity role2 = new RoleEntity(2, "ROLE_ADMIN");
        Set<RoleEntity> roles = Set.of(role1, role2);

        Set<RoleSecurity> roleSecuritySet = UserSecurityMapper.toRolesSecurity(roles);

        assertNotNull(roleSecuritySet);
        assertEquals(2, roleSecuritySet.size());
        assertTrue(roleSecuritySet.stream().anyMatch(r -> r.getId() == 1 && "ROLE_USER".equals(r.getName())));
        assertTrue(roleSecuritySet.stream().anyMatch(r -> r.getId() == 2 && "ROLE_ADMIN".equals(r.getName())));
    }

    @Test
    void testToUserSecurity() {
        Timestamp now = Timestamp.valueOf(LocalDateTime.now());
        UserEntity userEntity = new UserEntity(
                123L, "testuser", "test@example.com", "password123",
                now, now, true, false, 5
        );
        RoleEntity role1 = new RoleEntity(1, "ROLE_USER");
        Set<RoleEntity> roles = Set.of(role1);

        UserSecurity userSecurity = UserSecurityMapper.toUserSecurity(userEntity, roles);

        assertNotNull(userSecurity);
        assertEquals(123L, userSecurity.getId());
        assertEquals("testuser", userSecurity.getName());
        assertEquals("test@example.com", userSecurity.getEmail());
        assertEquals("password123", userSecurity.getPassword());
        assertEquals(now, userSecurity.getCreatedAt());
        assertEquals(now, userSecurity.getUpdatedAt());
        assertTrue(userSecurity.getVerified());
        assertFalse(userSecurity.getBlocked());
        assertEquals(5, userSecurity.getTokenVersion());
        
        Set<RoleSecurity> authorities = userSecurity.getRoles();
        assertNotNull(authorities);
        assertEquals(1, authorities.size());
        RoleSecurity authority = authorities.iterator().next();
        assertEquals(1, authority.getId());
        assertEquals("ROLE_USER", authority.getName());
    }
}
