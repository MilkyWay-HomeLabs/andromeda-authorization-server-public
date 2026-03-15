package org.derleta.authorization.security.filter;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.derleta.authorization.security.model.RoleSecurity;
import org.derleta.authorization.security.model.UserSecurity;
import org.derleta.authorization.security.util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * JwtTokenFilter is a filter that intercepts HTTP requests to enable JWT-based authentication.
 * It extends OncePerRequestFilter and processes each request to extract and validate JWT tokens.
 * If a valid token is found, it sets the authentication context in the SecurityContextHolder.
 */
@Component
public class JwtTokenFilter extends OncePerRequestFilter {

    private JwtTokenUtil jwtUtil;

    @Autowired
    public void setJwtUtil(JwtTokenUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String token = getTokenFromRequest(request);
        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }

        Claims claims;
        try {
            if (!jwtUtil.validateJWTToken(token)) {
                filterChain.doFilter(request, response);
                return;
            }
            claims = jwtUtil.parseClaims(token);
        } catch (RuntimeException ex) {
            filterChain.doFilter(request, response);
            return;
        }

        setAuthenticationContext(claims, request);
        filterChain.doFilter(request, response);
    }
    /**
     * Extracts the token from an HTTP request.
     * The method first checks the "Authorization" header for a token in the format "Bearer <token>"
     * and retrieves it if available. If not found, it then looks for a "jwtToken" cookie and extracts its value.
     *
     * @param request the HttpServletRequest object containing the request information.
     * @return the extracted token as a String if found in the "Authorization" header or "token" cookie;
     * returns null if no token is present in either location.
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (!ObjectUtils.isEmpty(header) && header.startsWith("Bearer ")) {
            return header.split("\\s", 2)[1].trim();
        }

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("jwtToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    private void setAuthenticationContext(Claims claims, HttpServletRequest request) {
        UserDetails userDetails = getUserDetails(claims);
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private UserDetails getUserDetails(Claims claims) {
        UserSecurity userDetails = new UserSecurity();

        String subject = claims.getSubject();
        if (subject == null || subject.isBlank()) {
            throw new IllegalArgumentException("Missing subject (sub) in token");
        }

        userDetails.setId(Long.parseLong(subject.trim()));

        for (String role : extractRoleNames(claims.get("roles"))) {
            if (role != null && !role.isBlank()) {
                userDetails.addRole(new RoleSecurity(role.trim()));
            }
        }

        return userDetails;
    }

    @SuppressWarnings("unchecked")
    private List<String> extractRoleNames(Object rolesClaim) {
        if (rolesClaim == null) return List.of();

        if (rolesClaim instanceof List<?> list) {
            List<String> out = new ArrayList<>();
            for (Object item : list) {
                if (item instanceof String s) {
                    out.add(s);
                } else if (item != null) {
                    out.add(item.toString());
                }
            }
            return out;
        }

        String s = rolesClaim.toString();
        if (s.isBlank()) return List.of();

        s = s.replace("[", "").replace("]", "");
        if (s.contains("name=")) {
            List<String> out = new ArrayList<>();
            String[] parts = s.split("},");
            for (String item : parts) {
                int startIndex = item.indexOf("name=") + 5;
                if (startIndex >= 5 && startIndex <= item.length()) {
                    String roleName = item.substring(startIndex).trim().replace("}", "");
                    out.add(roleName);
                }
            }
            return out;
        }

        String[] csv = s.split(",");
        List<String> out = new ArrayList<>();
        for (String item : csv) out.add(item.trim());
        return out;
    }

}
