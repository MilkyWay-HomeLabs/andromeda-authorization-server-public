package org.derleta.authorization.security.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A filter that processes incoming HTTP requests to validate the requesting
 * application's access permissions based on predefined allowed applications.
 * <p>
 * This filter checks for the presence of the "X-Requesting-App" header in the
 * HTTP request and compares its value against a list of allowed applications
 * defined in the application configuration. Requests originating from
 * unauthorized applications will be denied access.
 * <p>
 * The filter executes as the first filter in the chain due to its ordering
 * being set to 1. If the requesting application is allowed, it passes the
 * request through to the next filter in the chain; otherwise, it returns a
 * 403 response with an "Access denied" message.
 */
@Component
@Order(1)
public class AppFilter extends HttpFilter {

    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    private static final List<String> EXCLUDED_GET_URIS = List.of(
            "/api/v1/hello",
            "/api/v1/version"
    );

    private static final List<String> EXCLUDED_GET_PATTERNS = List.of(
            "/api/actuator/**",
            "/actuator/**"
    );

    private final Set<String> allowedApps;

    public AppFilter(@Value("${allowed.applications:}") List<String> allowedApplications) {
        this.allowedApps = allowedApplications.stream()
                .filter(s -> s != null && !s.isBlank())
                .map(String::trim)
                .collect(Collectors.toUnmodifiableSet());
    }

    /**
     * Filters incoming HTTP requests to validate if the requesting application has access permissions.
     * Checks for the presence of the "X-Requesting-App" header in the HTTP request, and processes or blocks
     * the request based on whether the application is allowed.
     * <p>
     * If the application is authorized, the filter passes the request to the next filter in the chain.
     * If the application is unauthorized, the filter returns a 403 response with an error message.
     *
     * @param request  the HTTP request containing the "X-Requesting-App" header
     * @param response the HTTP response to be sent to the client
     * @param chain    the filter chain used to pass the request and response to the next filter
     * @throws IOException      if an I/O error occurs during the processing of the request or response
     * @throws ServletException if an error occurs while processing the servlet
     */
    @Override
    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {

        if (isExcluded(request)) {
            chain.doFilter(request, response);
            return;
        }

        String requestingApp = request.getHeader("X-Requesting-App");
        if (isApplicationAllowed(requestingApp)) {
            chain.doFilter(request, response);
            return;
        }

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.getWriter().write("{\"message\":\"Access denied for the application\"}");
    }

    private boolean isExcluded(HttpServletRequest request) {
        if (!"GET".equalsIgnoreCase(request.getMethod())) {
            return false;
        }

        String uri = request.getRequestURI();

        if (EXCLUDED_GET_URIS.contains(uri)) {
            return true;
        }

        for (String pattern : EXCLUDED_GET_PATTERNS) {
            if (PATH_MATCHER.match(pattern, uri)) {
                return true;
            }
        }

        return false;
    }

    private boolean isApplicationAllowed(String requestingApp) {
        if (requestingApp == null || requestingApp.isBlank()) {
            return false;
        }
        return allowedApps.contains(requestingApp.trim());
    }

}
