package org.derleta.authorization.security.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.derleta.authorization.domain.model.ConfirmationTokenResult;
import org.derleta.authorization.security.model.RefreshTokenResult;
import org.derleta.authorization.security.model.UserSecurity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;

/**
 * Utility class for handling JSON Web Tokens (JWT) in the application.
 * This class provides methods for generating, validating, and parsing JWTs.
 * It is designed to support token-based authentication securely.
 */
@Component
public class JwtTokenUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtTokenUtil.class);

    private static final String CLAIM_VERSION = "version";
    private static final String CLAIM_REVOKED = "revoked";
    private static final String CLAIM_SESSION_EXP = "session_exp";

    @Value("${app.jwt.secret}")
    private String SECRET_KEY;

    @Value("${app.jwt.expiration.access}")
    public Integer JWT_ACCESS_EXPIRATION;

    @Value("${app.jwt.expiration.refresh}")
    public Long JWT_REFRESH_EXPIRATION;

    @Value("${app.jwt.expiration.confirmation}")
    private long JWT_CONFIRMATION_EXPIRATION;

    @Value("${app.jwt.expiration.session}")
    public Long JWT_SESSION_EXPIRATION;

    @PostConstruct
    void validateConfig() {
        if (SECRET_KEY.isBlank()) {
            throw new IllegalStateException("Missing property `app.jwt.secret` \\(= env APP_JWT_SECRET\\)");
        }

        try {
            byte[] decoded = Decoders.BASE64.decode(SECRET_KEY);
            if (decoded.length < 32) {
                throw new IllegalStateException("`app.jwt.secret` is too short after Base64 decode; need at least 32 bytes \\(256\\-bit\\)");
            }
        } catch (Exception e) {
            throw new IllegalStateException("`app.jwt.secret` is not valid Base64", e);
        }

        if (JWT_ACCESS_EXPIRATION <= 0) throw new IllegalStateException("Invalid `app.jwt.expiration.access`");
        if (JWT_REFRESH_EXPIRATION <= 0) throw new IllegalStateException("Invalid `app.jwt.expiration.refresh`");
        if (JWT_CONFIRMATION_EXPIRATION <= 0) throw new IllegalStateException("Invalid `app.jwt.expiration.confirmation`");
        if (JWT_SESSION_EXPIRATION <= 0) throw new IllegalStateException("Invalid `app.jwt.expiration.session`");
    }

    public String generateAccessToken(UserSecurity user) {
        return generateToken(user, JWT_ACCESS_EXPIRATION);
    }

    public RefreshTokenResult generateRefreshToken(UserSecurity user, java.util.Date sessionExp) {
        return generateRefresh(user, sessionExp);
    }

    public RefreshTokenResult generateRefreshToken(UserSecurity user) {
        java.util.Date now = new java.util.Date();
        java.util.Date sessionExp = new java.util.Date(now.getTime() + JWT_SESSION_EXPIRATION);
        return generateRefresh(user, sessionExp);
    }

    public ConfirmationTokenResult generateConfirmationToken(final Long userId, final int tokenVersion) {
        String jti = UUID.randomUUID().toString();
        java.util.Date now = new java.util.Date();

        long expiration = now.getTime() + JWT_CONFIRMATION_EXPIRATION;
        java.util.Date exp = new java.util.Date(expiration);

        String jwt = Jwts.builder()
                .id(jti)
                .subject(String.valueOf(userId))
                .issuer("AndromedaAuthApi")
                .claim(CLAIM_VERSION, tokenVersion)
                .claim(CLAIM_REVOKED, false)
                .claim(CLAIM_SESSION_EXP, exp)
                .issuedAt(now)
                .expiration(exp)
                .signWith(getPublicSigningKey())
                .compact();

        return new ConfirmationTokenResult(jwt, jti, exp, exp);
    }

    private String generateToken(UserSecurity user, long expiration) {
        String jti = java.util.UUID.randomUUID().toString(); // FIXME: need to verify is this unique enough?
        java.util.Date now = new java.util.Date();
        java.util.Date expiresAt = new java.util.Date(now.getTime() + expiration);

        return Jwts.builder()
                .id(jti)
                .subject(String.valueOf(user.getId()))
                .issuer("AndromedaAuthApi")
                .claim("roles", user.getRoles())
                .claim(CLAIM_VERSION, user.getTokenVersion())
                .claim(CLAIM_REVOKED, false)
                .issuedAt(now)
                .expiration(expiresAt)
                .signWith(getPublicSigningKey())
                .compact();
    }

    private RefreshTokenResult generateRefresh(UserSecurity user, java.util.Date sessionExp) {
        String jti = UUID.randomUUID().toString();
        java.util.Date now = new java.util.Date();

        long candidateExpMs = now.getTime() + JWT_REFRESH_EXPIRATION;
        long hardExpMs = sessionExp.getTime();
        java.util.Date exp = new java.util.Date(Math.min(candidateExpMs, hardExpMs));

        String jwt = Jwts.builder()
                .id(jti)
                .subject(String.valueOf(user.getId()))
                .issuer("AndromedaAuthApi")
                .claim("roles", user.getRoles())
                .claim(CLAIM_VERSION, user.getTokenVersion())
                .claim(CLAIM_REVOKED, false)
                .claim(CLAIM_SESSION_EXP, sessionExp.getTime())
                .issuedAt(now)
                .expiration(exp)
                .signWith(getPublicSigningKey())
                .compact();

        return new RefreshTokenResult(jwt, jti, exp, sessionExp);
    }


    /**
     * Validates the given JSON Web Token (JWT) to ensure it is properly formed, unexpired,
     * and signed with a valid key. This method checks the token's signature, format, and
     * expiration status and logs an error message for any detected issues.
     *
     * @param token the JWT as a string to be validated
     * @return true if the token is valid, false if the token is invalid, expired, null, or improperly formatted
     */
    public boolean validateJWTToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getPublicSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return true;
        } catch (ExpiredJwtException ex) {
            LOGGER.error("JWT expired : {}", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            LOGGER.error("Token is null, empty or only whitespace : {}", ex.getMessage());
        } catch (MalformedJwtException ex) {
            LOGGER.error("JWT is invalid", ex);
        } catch (UnsupportedJwtException ex) {
            LOGGER.error("JWT is not supported", ex);
        }
        return false;
    }

    /**
     * Retrieves the secret key used for signing JWT tokens.
     * The key is derived from a base64-encoded secret string.
     *
     * @return the secret key used for HMAC signing of JWT tokens.
     */
    private SecretKey getPublicSigningKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_KEY));
    }

    /**
     * Parses the given JSON Web Token (JWT) to extract the claims it contains.
     * The method uses a public signing key to verify the token's integrity and authenticity
     * before decoding and returning its claims.
     *
     * @param token the JWT as a string to be parsed. This token must be properly signed
     *              and encoded to allow successful verification and extraction of claims.
     * @return the claims extracted from the provided token. These claims may include properties
     * such as the token's subject, expiration, roles, and custom-defined attributes.
     */
    public Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(getPublicSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Retrieves the expiration date of a provided JWT token.
     * The method parses the token to extract its claims and returns
     * the expiration date contained within the claims.
     *
     * @param token the JWT as a string. This token must be properly formatted
     *              and signed to allow successful extraction of the expiration date.
     * @return the expiration date of the token as a Date object.
     */
    public Date getTokenExpiration(String token) {
        Claims claims = parseClaims(token);
        return new Date(claims.getExpiration().getTime());
    }

    /**
     * Extracts the user ID from the provided JWT token.
     * The user ID is assumed to be the first element in the token's subject, separated by a comma.
     *
     * @param token the JWT token from which to extract the user ID
     * @return the extracted user ID as a {@code Long}
     */
    public Long getUserId(String token) {
        final Claims claims = getAllClaimsFromToken(token);
        return Long.valueOf(claims.getSubject().split(",")[0]);
    }

    /**
     * Reads the JWT ID (jti) claim from the provided token, which serves as a unique identifier for the token.
     */
    public String getJti(String token) {
        final Claims claims = getAllClaimsFromToken(token);
        return claims.getId();
    }

    public Integer getTokenVersion(String token) {
        final Claims claims = getAllClaimsFromToken(token);
        Object value = claims.get(CLAIM_VERSION);

        if (value == null) {
            return null;
        }
        if (value instanceof Number n) {
            return n.intValue();
        }

        String s = value.toString().trim();
        if (s.isEmpty()) {
            return null;
        }

        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Incorrect claim `version`: " + value, e);
        }
    }

    /**
     * Reads the revoked claim from the provided token.
     */
    public boolean isRevokedClaim(String token) {
        final Claims claims = getAllClaimsFromToken(token);
        Object value = claims.get(CLAIM_REVOKED);
        if (value == null) return false;
        if (value instanceof Boolean b) return b;
        return Boolean.parseBoolean(String.valueOf(value));
    }

    /**
     * Extracts all claims from a given JWT token.
     *
     * @param token the JWT token from which to extract the claims
     * @return a {@code Claims} object containing all claims extracted from the token
     */
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().verifyWith(getPublicSigningKey()).build().parseSignedClaims(token).getPayload();
    }

    public java.util.Date getSessionExpiration(final String token) {
        final Claims claims = parseClaims(token);
        final Object v = claims.get(CLAIM_SESSION_EXP);
        if (v == null) return null;

        final long ms = (v instanceof Number)
                ? ((Number) v).longValue()
                : Long.parseLong(v.toString());

        return new java.util.Date(ms);
    }

}
