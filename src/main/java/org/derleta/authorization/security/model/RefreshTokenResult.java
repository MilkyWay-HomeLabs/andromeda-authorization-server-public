package org.derleta.authorization.security.model;

import java.util.Date;

public record RefreshTokenResult(String token, String jti, Date expiresAt, Date sessionExpiresAt) {
}
