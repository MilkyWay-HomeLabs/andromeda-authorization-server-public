package org.derleta.authorization.utils;

import org.derleta.authorization.domain.entity.UserEntity;
import org.derleta.authorization.domain.entity.token.ConfirmationTokenEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

class MailGeneratorTest {

    private MailGenerator mailGenerator;
    private final String testUrl = "https://example.com/confirm/";

    @BeforeEach
    void setUp() {
        mailGenerator = new MailGenerator();
        // The NEBULA_CONFIRMATION_MAIL_URL is static in MailGenerator, 
        // but it has a non-static @Value setter. 
        // We'll call the setter directly for the test.
        mailGenerator.setNEBULA_FRONT_APP_URL_CONFIRMATION(testUrl);
    }

    @Test
    void testGenerateVerificationMailText() {
        UserEntity user = Mockito.mock(UserEntity.class);
        when(user.getUsername()).thenReturn("john_doe");

        ConfirmationTokenEntity token = Mockito.mock(ConfirmationTokenEntity.class);
        long tokenId = 12345L;
        String tokenStr = "secret-token";
        when(token.getTokenId()).thenReturn(tokenId);
        when(token.getToken()).thenReturn(tokenStr);

        String result = mailGenerator.generateVerificationMailText(user, token);

        assertTrue(result.contains("john_doe"));
        assertTrue(result.contains(testUrl + tokenId + "/" + tokenStr));
        assertTrue(result.contains("to complete please enter to link:"));
    }

    @Test
    void testGeneratePasswordMailText() {
        UserEntity user = Mockito.mock(UserEntity.class);
        when(user.getUsername()).thenReturn("john_doe");
        String password = "newStrongPassword123!";

        String result = mailGenerator.generatePasswordMailText(user, password);

        assertTrue(result.contains("john_doe"));
        assertTrue(result.contains(password));
        assertTrue(result.contains("Please change your password after login"));
    }

    @Test
    void testGenerateChangePasswdInfoMailText() {
        String result = mailGenerator.generateChangePasswdInfoMailText();
        assertTrue(result.contains("password was changed"));
        assertTrue(result.contains("restore your password in nebula immediately"));
    }

    @Test
    void testGetVerificationSubject() {
        assertEquals("Confirmation message", MailGenerator.getVerificationSubject());
    }

    @Test
    void testGetPasswordSubject() {
        assertEquals("New password message", MailGenerator.getPasswordSubject());
    }
}
