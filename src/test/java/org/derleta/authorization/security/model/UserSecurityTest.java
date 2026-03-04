package org.derleta.authorization.security.model;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

class UserSecurityTest {

    @Test
    void testFullConstructorAndGetters() {
        long id = 1L;
        String name = "Test User";
        String email = "test@example.com";
        String password = "password";
        Timestamp createdAt = new Timestamp(System.currentTimeMillis());
        Timestamp updatedAt = new Timestamp(System.currentTimeMillis());
        Boolean verified = true;
        Boolean blocked = false;
        int tokenVersion = 1;
        Set<RoleSecurity> roles = new HashSet<>();
        roles.add(new RoleSecurity(1, "ROLE_USER"));

        UserSecurity user = new UserSecurity(id, name, email, password, createdAt, updatedAt, verified, blocked, tokenVersion, roles);

        assertEquals(id, user.getId());
        assertEquals(name, user.getName());
        assertEquals(email, user.getEmail());
        assertEquals(password, user.getPassword());
        assertEquals(createdAt, user.getCreatedAt());
        assertEquals(updatedAt, user.getUpdatedAt());
        assertEquals(verified, user.getVerified());
        assertEquals(blocked, user.getBlocked());
        assertEquals(tokenVersion, user.getTokenVersion());
        assertEquals(roles, user.getRoles());
    }

    @Test
    void testSetters() {
        UserSecurity user = new UserSecurity();
        user.setId(2L);
        user.setName("New Name");
        user.setEmail("new@example.com");
        user.setPassword("newpass");
        Timestamp now = new Timestamp(System.currentTimeMillis());
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        user.setVerified(false);
        user.setBlocked(true);
        user.setTokenVersion(5);
        Set<RoleSecurity> roles = new HashSet<>();
        user.setRoles(roles);

        assertEquals(2L, user.getId());
        assertEquals("New Name", user.getName());
        assertEquals("new@example.com", user.getEmail());
        assertEquals("newpass", user.getPassword());
        assertEquals(now, user.getCreatedAt());
        assertEquals(now, user.getUpdatedAt());
        assertFalse(user.getVerified());
        assertTrue(user.getBlocked());
        assertEquals(5, user.getTokenVersion());
        assertEquals(roles, user.getRoles());
    }

    @Test
    void testAddRole() {
        UserSecurity user = new UserSecurity();
        user.setRoles(new HashSet<>());
        RoleSecurity role = new RoleSecurity(1, "ROLE_ADMIN");
        user.addRole(role);

        assertTrue(user.getRoles().contains(role));
        assertEquals(1, user.getRoles().size());
    }

    @Test
    void testUserDetailsMethods() {
        UserSecurity user = new UserSecurity();
        user.setName("testuser");
        user.setVerified(true);
        user.setBlocked(false);

        assertEquals("testuser", user.getUsername());
        assertTrue(user.isAccountNonExpired());
        assertTrue(user.isCredentialsNonExpired());
        assertTrue(user.isAccountNonLocked()); // Implementation returns !blocked
        assertTrue(user.isEnabled()); // Implementation returns verified

        user.setBlocked(true);
        assertFalse(user.isAccountNonLocked());

        user.setVerified(false);
        assertFalse(user.isEnabled());
    }

    @Test
    void testGetAuthorities() {
        UserSecurity user = new UserSecurity();
        Set<RoleSecurity> roles = new HashSet<>();
        roles.add(new RoleSecurity(1, "ROLE_USER"));
        roles.add(new RoleSecurity(2, "ROLE_ADMIN"));
        user.setRoles(roles);

        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
        assertEquals(2, authorities.size());
        assertTrue(authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
        assertTrue(authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test
    void testGetAuthoritiesWithNullRoles() {
        UserSecurity user = new UserSecurity();
        user.setRoles(null);
        assertNotNull(user.getAuthorities());
        assertTrue(user.getAuthorities().isEmpty());
    }

    @Test
    void testToString() {
        UserSecurity user = new UserSecurity();
        user.setId(1L);
        user.setName("Name");
        user.setEmail("email@test.com");
        String toString = user.toString();
        assertTrue(toString.contains("UsersSecurity"));
        assertTrue(toString.contains("id=1"));
        assertTrue(toString.contains("name='Name'"));
        assertTrue(toString.contains("email='email@test.com'"));
    }

    @Test
    void testEqualsAndHashCode() {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        UserSecurity user1 = new UserSecurity(1L, "N", "E", "P", now, now, true, false, 1, null);
        UserSecurity user2 = new UserSecurity(1L, "N", "E", "P", now, now, true, false, 1, null);
        UserSecurity user3 = new UserSecurity(2L, "N", "E", "P", now, now, true, false, 1, null);

        assertEquals(user1, user2);
        assertNotEquals(user1, user3);
        assertNotEquals(null, user1);
        assertEquals(user1.hashCode(), user2.hashCode());
    }
}
