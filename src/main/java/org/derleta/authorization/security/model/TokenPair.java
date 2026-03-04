package org.derleta.authorization.security.model;

public record TokenPair(String accessToken, String refreshToken) {
}
