package org.derleta.authorization.domain.entity.token;


import java.sql.Timestamp;
import java.util.Objects;

public class RefreshTokenIncidentEntity {

    private Long id;
    private Long tokenId;
    private Long userId;
    private String jti;
    private int version;
    private Timestamp incidentTime;
    private String ipAddress;
    private String userAgent;
    private String description;

    public RefreshTokenIncidentEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTokenId() {
        return tokenId;
    }

    public void setTokenId(Long tokenId) {
        this.tokenId = tokenId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getJti() {
        return jti;
    }

    public void setJti(String jti) {
        this.jti = jti;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public Timestamp getIncidentTime() {
        return incidentTime;
    }

    public void setIncidentTime(Timestamp incidentTime) {
        this.incidentTime = incidentTime;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        RefreshTokenIncidentEntity that = (RefreshTokenIncidentEntity) o;
        return Objects.equals(id, that.id) && Objects.equals(tokenId, that.tokenId) && Objects.equals(userId, that.userId) && Objects.equals(jti, that.jti) && Objects.equals(version, that.version) && Objects.equals(incidentTime, that.incidentTime) && Objects.equals(ipAddress, that.ipAddress) && Objects.equals(userAgent, that.userAgent) && Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(id);
        result = 31 * result + Objects.hashCode(tokenId);
        result = 31 * result + Objects.hashCode(userId);
        result = 31 * result + Objects.hashCode(jti);
        result = 31 * result + Objects.hashCode(version);
        result = 31 * result + Objects.hashCode(incidentTime);
        result = 31 * result + Objects.hashCode(ipAddress);
        result = 31 * result + Objects.hashCode(userAgent);
        result = 31 * result + Objects.hashCode(description);
        return result;
    }

    @Override
    public String toString() {
        return "RefreshTokenIncidentEntity{" +
                "id=" + id +
                ", tokenId=" + tokenId +
                ", userId=" + userId +
                ", jti='" + jti + '\'' +
                ", version='" + version + '\'' +
                ", incidentTime=" + incidentTime +
                ", ipAddress='" + ipAddress + '\'' +
                ", userAgent='" + userAgent + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
