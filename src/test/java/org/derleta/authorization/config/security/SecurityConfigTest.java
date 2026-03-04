package org.derleta.authorization.config.security;

import org.derleta.authorization.repository.impl.TokensGeneratorRepository;
import org.derleta.authorization.security.filter.JwtTokenFilter;
import org.derleta.authorization.security.model.UserSecurity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SecurityConfigTest {

    @Mock
    private TokensGeneratorRepository userRepo;

    @Mock
    private JwtTokenFilter jwtTokenFilter;

    @InjectMocks
    private SecurityConfig securityConfig;

    @BeforeEach
    void setUp() {
        securityConfig.setUserRepo(userRepo);
        securityConfig.setJwtTokenFilter(jwtTokenFilter);
    }

    @Test
    void testAuthenticationProvider() {
        DaoAuthenticationProvider provider = securityConfig.authenticationProvider();
        assertNotNull(provider, "AuthenticationProvider should not be null");
    }

    @Test
    void testPasswordEncoder() {
        PasswordEncoder encoder = securityConfig.passwordEncoder();
        assertNotNull(encoder, "PasswordEncoder should not be null");
    }

    @Test
    void testUserDetailsService_FoundByEmail() {
        String email = "test@example.com";
        UserSecurity mockUser = mock(UserSecurity.class);
        when(mockUser.getEmail()).thenReturn(email);
        when(userRepo.findByEmail(email)).thenReturn(Optional.of(mockUser));

        UserDetailsService userDetailsService = securityConfig.userDetailsService();
        assertNotNull(userDetailsService.loadUserByUsername(email));
    }

    @Test
    void testUserDetailsService_FoundByLogin() {
        String login = "testuser";
        UserSecurity mockUser = mock(UserSecurity.class);
        when(userRepo.findByEmail(login)).thenReturn(Optional.empty());
        when(userRepo.findByLogin(login)).thenReturn(Optional.of(mockUser));

        UserDetailsService userDetailsService = securityConfig.userDetailsService();
        assertNotNull(userDetailsService.loadUserByUsername(login));
    }

    @Test
    void testUserDetailsService_NotFound() {
        String username = "unknown";
        when(userRepo.findByEmail(username)).thenReturn(Optional.empty());
        when(userRepo.findByLogin(username)).thenReturn(Optional.empty());

        UserDetailsService userDetailsService = securityConfig.userDetailsService();
        assertThrows(UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername(username));
    }

    @Test
    void testAuthenticationManager() throws Exception {
        AuthenticationConfiguration authConfig = mock(AuthenticationConfiguration.class);
        AuthenticationManager authManager = mock(AuthenticationManager.class);
        when(authConfig.getAuthenticationManager()).thenReturn(authManager);

        AuthenticationManager result = securityConfig.authenticationManager(authConfig);
        assertNotNull(result);
    }

    @Test
    void testCorsConfigurationSource() {
        CorsConfigurationSource source = securityConfig.corsConfigurationSource();
        assertNotNull(source);
    }

}
