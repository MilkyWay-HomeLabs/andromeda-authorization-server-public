package org.derleta.authorization.domain.model;

import java.util.Date;

public record ConfirmationTokenResult(String token, String jti, Date expiresAt, Date sessionExpiresAt) {
}
