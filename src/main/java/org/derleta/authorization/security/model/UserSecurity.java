package org.derleta.authorization.security.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The UserSecurity class implements the UserDetails interface from the Spring Security framework.
 * It represents a user entity with security-specific attributes, such as roles and permissions.
 * This class manages user identity and security information required for authentication and authorization processes.
 */
public class UserSecurity implements UserDetails {

    private long id;
    private String name;
    private String email;
    private String password;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private Boolean verified;
    private Boolean blocked;
    private int tokenVersion;

    private Set<RoleSecurity> roles = new HashSet<>();

    public UserSecurity() {
    }

    public UserSecurity(long id, String name, String email, String password,
                        Timestamp createdAt, Timestamp updatedAt, Boolean verified, Boolean blocked, int tokenVersion,
                        Set<RoleSecurity> roles) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.verified = verified;
        this.blocked = blocked;
        this.tokenVersion = tokenVersion;
        this.roles = roles;
    }

    /**
     * Retrieves the authorities granted to the user based on their roles.
     * Each role is mapped to a {@code SimpleGrantedAuthority}, ensuring
     * uniqueness by filtering out duplicate authorities.
     *
     * @return a collection of granted authorities derived from the user's roles
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (roles == null) {
            return new HashSet<>();
        }
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public String getUsername() {
        return this.name;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.blocked == null || !this.blocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.verified != null && this.verified;
    }

    public void addRole(RoleSecurity roleSecurity) {
        this.roles.add(roleSecurity);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<RoleSecurity> getRoles() {
        return roles;
    }

    public void setRoles(Set<RoleSecurity> roles) {
        this.roles = roles;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

    public Boolean getBlocked() {
        return blocked;
    }

    public void setBlocked(Boolean blocked) {
        this.blocked = blocked;
    }

    public int getTokenVersion() {
        return tokenVersion;
    }

    public void setTokenVersion(int tokenVersion) {
        this.tokenVersion = tokenVersion;
    }

    @Override
    public String toString() {
        return "UsersSecurity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", verified=" + verified +
                ", blocked=" + blocked +
                ", tokenVersion=" + tokenVersion +
                ", roles=" + roles +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserSecurity that)) return false;
        if (id != that.id) return false;
        if (!Objects.equals(name, that.name)) return false;
        if (!Objects.equals(email, that.email)) return false;
        if (!Objects.equals(password, that.password)) return false;
        if (!Objects.equals(createdAt, that.createdAt)) return false;
        if (!Objects.equals(updatedAt, that.updatedAt)) return false;
        if (!Objects.equals(verified, that.verified)) return false;
        if (!Objects.equals(blocked, that.blocked)) return false;
        if (tokenVersion != that.tokenVersion) return false;
        return Objects.equals(roles, that.roles);
    }

    @Override
    public int hashCode() {
        int result = Long.hashCode(id);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (roles != null ? roles.hashCode() : 0);
        result = 31 * result + (createdAt != null ? createdAt.hashCode() : 0);
        result = 31 * result + (updatedAt != null ? updatedAt.hashCode() : 0);
        result = 31 * result + (verified != null ? verified.hashCode() : 0);
        result = 31 * result + (blocked != null ? blocked.hashCode() : 0);
        result = 31 * result + tokenVersion;
        return result;
    }

}
