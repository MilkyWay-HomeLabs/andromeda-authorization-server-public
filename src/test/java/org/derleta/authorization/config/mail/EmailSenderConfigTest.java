package org.derleta.authorization.config.mail;

import org.junit.jupiter.api.Test;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class EmailSenderConfigTest {

    @Test
    void testGetJavaMailSender() {
        // Given
        EmailSenderConfig config = new EmailSenderConfig();
        ReflectionTestUtils.setField(config, "host", "smtp.example.com");
        ReflectionTestUtils.setField(config, "port", 587);
        ReflectionTestUtils.setField(config, "username", "user@example.com");
        ReflectionTestUtils.setField(config, "password", "password123");

        // When
        JavaMailSender javaMailSender = config.getJavaMailSender();

        // Then
        assertNotNull(javaMailSender, "JavaMailSender should not be null");
        assertInstanceOf(JavaMailSenderImpl.class, javaMailSender, "Should be an instance of JavaMailSenderImpl");

        JavaMailSenderImpl mailSenderImpl = (JavaMailSenderImpl) javaMailSender;
        assertEquals("smtp.example.com", mailSenderImpl.getHost());
        assertEquals(587, mailSenderImpl.getPort());
        assertEquals("user@example.com", mailSenderImpl.getUsername());
        assertEquals("password123", mailSenderImpl.getPassword());

        Properties props = mailSenderImpl.getJavaMailProperties();
        assertEquals("smtp", props.getProperty("mail.transport.protocol"));
        assertEquals("true", props.getProperty("mail.smtp.auth"));
        assertEquals("true", props.getProperty("mail.smtp.starttls.enable"));
        assertEquals("true", props.getProperty("mail.debug"));
    }
}
