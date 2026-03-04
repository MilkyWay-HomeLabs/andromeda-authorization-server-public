package org.derleta.authorization.domain.entity.token;

import java.sql.Timestamp;
import java.util.Objects;

public abstract class TokenEntity {

    protected long tokenId;
    protected long userId;
    protected String token;
    protected Timestamp expirationDate;
    protected int version;
    protected String jti;
    protected boolean revoked;

    public TokenEntity() {
    }

    public TokenEntity(long tokenId, long userId, String token, Timestamp expirationDate, int version, String jti, boolean revoked) {
        this.tokenId = tokenId;
        this.userId = userId;
        this.token = token;
        this.expirationDate = expirationDate;
        this.version = version;
        this.jti = jti;
        this.revoked = revoked;
    }

    public long getTokenId() {
        return tokenId;
    }

    public void setTokenId(long tokenId) {
        this.tokenId = tokenId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Timestamp getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Timestamp expirationDate) {
        this.expirationDate = expirationDate;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getJti() {
        return jti;
    }

    public void setJti(String jti) {
        this.jti = jti;
    }

    public boolean isRevoked() {
        return revoked;
    }

    public void setRevoked(boolean revoked) {
        this.revoked = revoked;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AccessTokenEntity that)) return false;
        if (tokenId != that.tokenId) return false;
        if (userId != that.userId) return false;
        if (!token.equals(that.token)) return false;
        if (version != that.version) return false;
        if (!Objects.equals(jti, that.jti)) return false;
        if (revoked != that.revoked) return false;
        return expirationDate.equals(that.expirationDate);
    }

    @Override
    public int hashCode() {
        int result = Long.hashCode(tokenId);
        result = 31 * result + Long.hashCode(userId);
        result = 31 * result + token.hashCode();
        result = 31 * result + expirationDate.hashCode();
        result = 31 * result + (version != 0 ? version : 1);
        result = 31 * result + (jti != null ? jti.hashCode() : 0);
        result = 31 * result + (revoked ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        String classname = this.getClass().getSimpleName();
        return classname +
                "tokenId=" + tokenId +
                ", userId=" + userId +
                ", token='" + token + '\'' +
                ", expirationDate=" + expirationDate +
                ", version='" + version + '\'' +
                ", jti='" + jti + '\'' +
                ", revoked=" + revoked +
                '}';
    }

}
