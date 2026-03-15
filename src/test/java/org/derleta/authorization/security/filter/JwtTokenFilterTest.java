package org.derleta.authorization.security.filter;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.derleta.authorization.security.model.UserSecurity;
import org.derleta.authorization.security.util.JwtTokenUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtTokenFilterTest {

    @Mock
    private JwtTokenUtil jwtUtil;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtTokenFilter jwtTokenFilter;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternal_NoToken_PassesThrough() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn(null);
        when(request.getCookies()).thenReturn(null);

        jwtTokenFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternal_ValidTokenInHeader_SetsAuthentication() throws ServletException, IOException {
        String token = "valid.token.here";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtUtil.validateJWTToken(token)).thenReturn(true);

        Claims claims = mock(Claims.class);
        when(claims.getSubject()).thenReturn("123");
        when(claims.get("roles")).thenReturn(List.of("ROLE_USER"));
        when(jwtUtil.parseClaims(token)).thenReturn(claims);

        jwtTokenFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        UserSecurity userDetails = (UserSecurity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        assertEquals(123L, userDetails.getId());
    }

    @Test
    void doFilterInternal_ValidTokenInCookie_SetsAuthentication() throws ServletException, IOException {
        String token = "valid.cookie.token";
        Cookie cookie = new Cookie("jwtToken", token);
        when(request.getCookies()).thenReturn(new Cookie[]{cookie});
        when(jwtUtil.validateJWTToken(token)).thenReturn(true);

        Claims claims = mock(Claims.class);
        when(claims.getSubject()).thenReturn("456");
        when(claims.get("roles")).thenReturn("ROLE_ADMIN");
        when(jwtUtil.parseClaims(token)).thenReturn(claims);

        jwtTokenFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        UserSecurity userDetails = (UserSecurity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        assertEquals(456L, userDetails.getId());
    }

    @Test
    void doFilterInternal_InvalidToken_DoesNotSetAuthentication() throws ServletException, IOException {
        String token = "invalid.token";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtUtil.validateJWTToken(token)).thenReturn(false);

        jwtTokenFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternal_JwtException_DoesNotSetAuthentication() throws ServletException, IOException {
        String token = "problematic.token";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtUtil.validateJWTToken(token)).thenThrow(new RuntimeException("Token error"));

        jwtTokenFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternal_TokenMissingSubject_ThrowsException() throws ServletException, IOException {
        String token = "no.subject.token";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtUtil.validateJWTToken(token)).thenReturn(true);

        Claims claims = mock(Claims.class);
        when(claims.getSubject()).thenReturn(null);
        when(jwtUtil.parseClaims(token)).thenReturn(claims);

        assertThrows(IllegalArgumentException.class, () ->
                jwtTokenFilter.doFilterInternal(request, response, filterChain)
        );
    }

    @Test
    void doFilterInternal_ExtractRolesComplexFormat_SetsAuthentication() throws ServletException, IOException {
        String token = "complex.roles.token";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtUtil.validateJWTToken(token)).thenReturn(true);

        Claims claims = mock(Claims.class);
        when(claims.getSubject()).thenReturn("789");
        // Format: [{authority=ROLE_USER}, {authority=ROLE_ADMIN}] or similar string representation handled by JwtTokenFilter
        when(claims.get("roles")).thenReturn("[{name=ROLE_USER}, {name=ROLE_ADMIN}]");
        when(jwtUtil.parseClaims(token)).thenReturn(claims);

        jwtTokenFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(2, SecurityContextHolder.getContext().getAuthentication().getAuthorities().size());
    }

    @Test
    void doFilterInternal_ExtractRolesCSV_SetsAuthentication() throws ServletException, IOException {
        String token = "csv.roles.token";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtUtil.validateJWTToken(token)).thenReturn(true);

        Claims claims = mock(Claims.class);
        when(claims.getSubject()).thenReturn("101");
        when(claims.get("roles")).thenReturn("ROLE_A, ROLE_B");
        when(jwtUtil.parseClaims(token)).thenReturn(claims);

        jwtTokenFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(2, SecurityContextHolder.getContext().getAuthentication().getAuthorities().size());
    }
}
