package org.derleta.authorization.domain.entity;

import java.sql.Timestamp;
import java.util.Objects;

public class UserEntity {

    private long userId;
    private String username;
    private String email;
    private String password;
    private java.sql.Timestamp createdAt;
    private java.sql.Timestamp updatedAt;
    private Boolean verified;
    private Boolean blocked;
    private int tokenVersion;

    public UserEntity(long userId, String name, String email, String password,
                      java.sql.Timestamp createdAt, java.sql.Timestamp updatedAt, Boolean verified, Boolean blocked, int tokenVersion) {
        this.userId = userId;
        this.username = name;
        this.email = email;
        this.password = password;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.verified = verified;
        this.blocked = blocked;
        this.tokenVersion = tokenVersion;
    }

    public UserEntity() {
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserEntity that)) return false;
        if (userId != that.userId) return false;
        if (!Objects.equals(username, that.username)) return false;
        if (!Objects.equals(email, that.email)) return false;
        if (!Objects.equals(createdAt, that.createdAt)) return false;
        if (!Objects.equals(updatedAt, that.updatedAt)) return false;
        if (!Objects.equals(verified, that.verified)) return false;
        if (!Objects.equals(blocked, that.blocked)) return false;
        if (tokenVersion != that.tokenVersion) return false;
        return Objects.equals(password, that.password);
    }

    @Override
    public int hashCode() {
        int result = Long.hashCode(userId);
        result = 31 * result + (username != null ? username.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (createdAt != null ? createdAt.hashCode() : 0);
        result = 31 * result + (updatedAt != null ? updatedAt.hashCode() : 0);
        result = 31 * result + (verified != null ? verified.hashCode() : 0);
        result = 31 * result + (blocked != null ? blocked.hashCode() : 0);
        result = 31 * result + tokenVersion;
        return result;
    }

    @Override
    public String toString() {
        return "UserEntity{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", verified=" + verified +
                ", blocked=" + blocked +
                ", tokenVersion=" + tokenVersion +
                '}';
    }

}
