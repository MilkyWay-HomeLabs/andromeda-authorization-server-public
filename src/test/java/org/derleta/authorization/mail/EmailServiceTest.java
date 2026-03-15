package org.derleta.authorization.mail;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender javaMailSender;

    private EmailService emailService;

    private final String senderEmail = "sender@example.com";

    @BeforeEach
    void setUp() {
        emailService = new EmailService(javaMailSender);
        ReflectionTestUtils.setField(emailService, "username", senderEmail);
    }

    @Test
    void testSendEmail_Success() {
        // Given
        String to = "recipient@example.com";
        String subject = "Test Subject";
        String text = "Test Body";

        // When
        emailService.sendEmail(to, subject, text);

        // Then
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(javaMailSender).send(messageCaptor.capture());

        SimpleMailMessage sentMessage = messageCaptor.getValue();
        assertEquals(senderEmail, sentMessage.getFrom());
        Assertions.assertNotNull(sentMessage.getTo());
        assertEquals(to, sentMessage.getTo()[0]);
        assertEquals(subject, sentMessage.getSubject());
        assertEquals(text, sentMessage.getText());
    }

    @Test
    void testSendEmail_NullRecipient() {
        assertThrows(IllegalArgumentException.class, () -> emailService.sendEmail(null, "Subject", "Text"));
    }

    @Test
    void testSendEmail_EmptyRecipient() {
        assertThrows(IllegalArgumentException.class, () -> emailService.sendEmail("", "Subject", "Text"));
    }

    @Test
    void testSendEmail_InvalidRecipientFormat() {
        assertThrows(IllegalArgumentException.class, () -> emailService.sendEmail("invalid-email", "Subject", "Text"));
    }

    @Test
    void testSendEmail_NullSubject() {
        assertThrows(IllegalArgumentException.class, () -> emailService.sendEmail("recipient@example.com", null, "Text"));
    }

    @Test
    void testSendEmail_EmptySubject() {
        assertThrows(IllegalArgumentException.class, () -> emailService.sendEmail("recipient@example.com", " ", "Text"));
    }

    @Test
    void testSendEmail_NullText() {
        assertThrows(IllegalArgumentException.class, () -> emailService.sendEmail("recipient@example.com", "Subject", null));
    }

    @Test
    void testSendEmail_EmptyText() {
        assertThrows(IllegalArgumentException.class, () -> emailService.sendEmail("recipient@example.com", "Subject", ""));
    }
}
