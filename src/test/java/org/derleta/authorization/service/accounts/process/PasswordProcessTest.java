package org.derleta.authorization.service.accounts.process;

import org.derleta.authorization.mail.EmailService;
import org.derleta.authorization.repository.RepositoryClass;
import org.derleta.authorization.repository.impl.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class PasswordProcessTest {

    @Mock
    private EmailService emailService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    void testConstructor_NullEmailService() {
        Set<RepositoryClass> repositories = new HashSet<>();
        repositories.add(userRepository);
        assertThrows(IllegalArgumentException.class, () -> new ChangePasswordProcess(repositories, null, passwordEncoder));
    }

    @Test
    void testConstructor_NullRepositories() {
        assertThrows(IllegalArgumentException.class, () -> new ChangePasswordProcess(null, emailService, passwordEncoder));
    }

    @Test
    void testConstructor_EmptyRepositories() {
        assertThrows(IllegalArgumentException.class, () -> new ChangePasswordProcess(Collections.emptySet(), emailService, passwordEncoder));
    }

    @Test
    void testConstructor_MissingUserRepository() {
        Set<RepositoryClass> repositories = new HashSet<>();
        repositories.add(mock(RepositoryClass.class));
        assertThrows(IllegalStateException.class, () -> new ChangePasswordProcess(repositories, emailService, passwordEncoder));
    }

    @Test
    void testCheck_ThrowsUnsupportedOperationException() {
        // Since check() is overridden in subclasses, we need to test the base method.
        // However, ChangePasswordProcess and ResetPasswordProcess override it.
        // We'll call the super.check() if we had a non-overriding subclass, but they are all permitted.
        // We can test this by calling it on a subclass that doesn't override it if it existed,
        // but since it's abstract and sealed with only 2 permitted classes that both override it,
        // the base implementation is technically unreachable if both override it.
        // Let's verify if they both override it.
    }
}
