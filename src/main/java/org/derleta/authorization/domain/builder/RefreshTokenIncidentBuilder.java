package org.derleta.authorization.domain.builder;

import org.derleta.authorization.domain.entity.token.RefreshTokenIncidentEntity;

import java.sql.Timestamp;


public interface RefreshTokenIncidentBuilder {

    RefreshTokenIncidentBuilder id(Long id);

    RefreshTokenIncidentBuilder tokenId(Long id);

    RefreshTokenIncidentBuilder userId(Long id);

    RefreshTokenIncidentBuilder jti(String jti);

    RefreshTokenIncidentBuilder version(int version);

    RefreshTokenIncidentBuilder incidentTime(Timestamp incidentTime);

    RefreshTokenIncidentBuilder ipAddress(String ipAddress);

    RefreshTokenIncidentBuilder userAgent(String userAgent);

    RefreshTokenIncidentBuilder description(String description);

    RefreshTokenIncidentEntity build();

}
