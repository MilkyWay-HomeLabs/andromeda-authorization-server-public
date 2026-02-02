package org.derleta.authorization.config.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.PrintWriter;

import static org.mockito.Mockito.*;

@SpringBootTest(properties = "allowed.applications=nebula-rest-api,chess-rest-api")
public class AppFilterTest {

    private static final String ACCESS_DENIED_JSON = "{\"message\":\"Access denied for the application\"}";

    @Test
    void doFilter_withAllowedApplication_shouldPassRequest() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        when(request.getHeader("X-Requesting-App")).thenReturn("nebula-rest-api");

        AppFilter appFilter = new AppFilter();
        ReflectionTestUtils.setField(appFilter, "allowedApplications", "nebula-rest-api");

        appFilter.doFilter(request, response, chain);

        verify(chain, times(1)).doFilter(request, response);
        verify(response, never()).setStatus(403);
    }

    @Test
    void doFilter_withNotAllowedApplication_shouldDenyRequest() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);
        PrintWriter writer = mock(PrintWriter.class);

        when(request.getHeader("X-Requesting-App")).thenReturn("UnknownApp");
        when(response.getWriter()).thenReturn(writer);

        AppFilter appFilter = new AppFilter();

        appFilter.doFilter(request, response, chain);

        verify(chain, never()).doFilter(request, response);
        verify(response, times(1)).setStatus(403);
        verify(writer, times(1)).write(ACCESS_DENIED_JSON);
    }

    @Test
    void doFilter_withMissingHeader_shouldDenyRequest() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);
        PrintWriter writer = mock(PrintWriter.class);

        when(request.getHeader("X-Requesting-App")).thenReturn(null);
        when(response.getWriter()).thenReturn(writer);

        AppFilter appFilter = new AppFilter();

        appFilter.doFilter(request, response, chain);

        verify(chain, never()).doFilter(request, response);
        verify(response, times(1)).setStatus(403);
        verify(writer, times(1)).write(ACCESS_DENIED_JSON);
    }

    @Test
    void doFilter_withNotSetAllowedApplications_shouldDenyRequest() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);
        PrintWriter writer = mock(PrintWriter.class);

        when(request.getHeader("X-Requesting-App")).thenReturn("App1");
        when(response.getWriter()).thenReturn(writer);

        AppFilter appFilter = new AppFilter();
        ReflectionTestUtils.setField(appFilter, "allowedApplications", null);

        appFilter.doFilter(request, response, chain);

        verify(chain, never()).doFilter(request, response);
        verify(response, times(1)).setStatus(403);
        verify(writer, times(1)).write(ACCESS_DENIED_JSON);
    }
}