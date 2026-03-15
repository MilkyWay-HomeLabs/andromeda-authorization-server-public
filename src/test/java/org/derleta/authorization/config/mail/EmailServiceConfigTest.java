package org.derleta.authorization.config.mail;

import org.derleta.authorization.mail.EmailService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class EmailServiceConfigTest {

    @Mock
    private JavaMailSender javaMailSender;

    @Test
    void testEmailServiceBeanCreation() {
        // Given
        EmailServiceConfig config = new EmailServiceConfig(javaMailSender);

        // When
        EmailService emailService = config.emailService();

        // Then
        assertNotNull(emailService, "EmailService should not be null");

        // Verify that the emailService is initialized with the mocked javaMailSender
        JavaMailSender internalSender = (JavaMailSender) ReflectionTestUtils.getField(emailService, "javaMailSender");
        assertEquals(javaMailSender, internalSender, "EmailService should use the provided JavaMailSender");
    }
}
