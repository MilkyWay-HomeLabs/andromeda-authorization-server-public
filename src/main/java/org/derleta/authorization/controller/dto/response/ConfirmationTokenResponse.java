package org.derleta.authorization.controller.dto.response;

import org.springframework.hateoas.RepresentationModel;

import java.sql.Timestamp;

public class ConfirmationTokenResponse extends RepresentationModel<ConfirmationTokenResponse> {

    private long tokenId;
    private long userId;
    private String token;
    private Timestamp expirationDate;
    private int version;
    private String jti;
    private boolean revoked;

    public ConfirmationTokenResponse() {
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
}
