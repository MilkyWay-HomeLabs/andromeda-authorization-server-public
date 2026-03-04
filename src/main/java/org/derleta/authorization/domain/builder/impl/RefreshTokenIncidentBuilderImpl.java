package org.derleta.authorization.domain.builder.impl;

import org.derleta.authorization.domain.builder.RefreshTokenIncidentBuilder;
import org.derleta.authorization.domain.entity.token.RefreshTokenIncidentEntity;

import java.sql.Timestamp;

public class RefreshTokenIncidentBuilderImpl implements RefreshTokenIncidentBuilder {

    private Long id;
    private Long tokenId;
    private Long userId;
    private String jti;
    private int version;
    private Timestamp incidentTime;
    private String ipAddress;
    private String userAgent;
    private String description;

    @Override
    public RefreshTokenIncidentBuilder id(Long id) {
        this.id = id;
        return this;
    }

    @Override
    public RefreshTokenIncidentBuilder tokenId(Long id) {
        this.tokenId = id;
        return this;
    }

    @Override
    public RefreshTokenIncidentBuilder userId(Long id) {
        this.userId = id;
        return this;
    }

    @Override
    public RefreshTokenIncidentBuilder jti(String jti) {
        this.jti = jti;
        return this;
    }

    @Override
    public RefreshTokenIncidentBuilder version(int version) {
        this.version = version;
        return this;
    }

    @Override
    public RefreshTokenIncidentBuilder incidentTime(Timestamp incidentTime) {
        this.incidentTime = incidentTime;
        return this;
    }

    @Override
    public RefreshTokenIncidentBuilder ipAddress(String ipAddress) {
        this.ipAddress = ipAddress;
        return this;
    }

    @Override
    public RefreshTokenIncidentBuilder userAgent(String userAgent) {
        this.userAgent = userAgent;
        return this;
    }

    @Override
    public RefreshTokenIncidentBuilder description(String description) {
        this.description = description;
        return this;
    }

    @Override
    public RefreshTokenIncidentEntity build() {
        RefreshTokenIncidentEntity incident = new RefreshTokenIncidentEntity();
        incident.setId(id);
        incident.setTokenId(tokenId);
        incident.setUserId(userId);
        incident.setJti(jti);
        incident.setVersion(version);
        incident.setIncidentTime(incidentTime);
        incident.setIpAddress(ipAddress);
        incident.setUserAgent(userAgent);
        incident.setDescription(description);
        return incident;
    }
}
