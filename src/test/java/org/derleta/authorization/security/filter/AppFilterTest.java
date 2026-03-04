package org.derleta.authorization.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class AppFilterTest {

    private AppFilter appFilter;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private FilterChain chain;
    private StringWriter stringWriter;
    private PrintWriter printWriter;

    @BeforeEach
    void setUp() throws IOException {
        List<String> allowedApps = List.of("App1", "App2");
        appFilter = new AppFilter(allowedApps);
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        chain = mock(FilterChain.class);
        stringWriter = new StringWriter();
        printWriter = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printWriter);
    }

    @Test
    void doFilter_AllowedApp_PassesThrough() throws IOException, ServletException {
        when(request.getHeader("X-Requesting-App")).thenReturn("App1");
        when(request.getMethod()).thenReturn("POST");

        appFilter.doFilter(request, response, chain);

        verify(chain, times(1)).doFilter(request, response);
        verify(response, never()).setStatus(anyInt());
    }

    @Test
    void doFilter_UnauthorizedApp_Forbidden() throws IOException, ServletException {
        when(request.getHeader("X-Requesting-App")).thenReturn("UnauthorizedApp");
        when(request.getMethod()).thenReturn("POST");

        appFilter.doFilter(request, response, chain);

        verify(chain, never()).doFilter(request, response);
        verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
        assertTrue(stringWriter.toString().contains("Access denied for the application"));
    }

    @Test
    void doFilter_MissingHeader_Forbidden() throws IOException, ServletException {
        when(request.getHeader("X-Requesting-App")).thenReturn(null);
        when(request.getMethod()).thenReturn("POST");

        appFilter.doFilter(request, response, chain);

        verify(chain, never()).doFilter(request, response);
        verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
    }

    @ParameterizedTest
    @ValueSource(strings = {"/api/v1/hello", "/api/v1/version"})
    void doFilter_ExcludedUri_GET_PassesThrough(String uri) throws IOException, ServletException {
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn(uri);

        appFilter.doFilter(request, response, chain);

        verify(chain, times(1)).doFilter(request, response);
        verify(response, never()).setStatus(anyInt());
    }

    @ParameterizedTest
    @ValueSource(strings = {"/api/actuator/health", "/actuator/info"})
    void doFilter_ExcludedPattern_GET_PassesThrough(String uri) throws IOException, ServletException {
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn(uri);

        appFilter.doFilter(request, response, chain);

        verify(chain, times(1)).doFilter(request, response);
        verify(response, never()).setStatus(anyInt());
    }

    @Test
    void doFilter_ExcludedUri_POST_NotExcluded() throws IOException, ServletException {
        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn("/api/v1/hello");
        when(request.getHeader("X-Requesting-App")).thenReturn(null);

        appFilter.doFilter(request, response, chain);

        verify(chain, never()).doFilter(request, response);
        verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
    }

    @Test
    void doFilter_AllowedAppWithTrim_PassesThrough() throws IOException, ServletException {
        when(request.getHeader("X-Requesting-App")).thenReturn(" App1 ");
        when(request.getMethod()).thenReturn("POST");

        appFilter.doFilter(request, response, chain);

        verify(chain, times(1)).doFilter(request, response);
    }
}
