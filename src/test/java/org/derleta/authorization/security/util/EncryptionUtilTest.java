package org.derleta.authorization.security.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class EncryptionUtilTest {

    private EncryptionUtil encryptionUtil;
    private final String VALID_KEY_16 = "1234567890123456";
    private final String VALID_KEY_24 = "123456789012345678901234";
    private final String VALID_KEY_32 = "12345678901234567890123456789012";

    @BeforeEach
    void setUp() {
        encryptionUtil = new EncryptionUtil();
        ReflectionTestUtils.setField(encryptionUtil, "encryptionKey", VALID_KEY_16);
    }

    @Test
    void testEncryptDecryptSuccess() {
        String originalText = "Hello World!";
        String encryptedText = encryptionUtil.encrypt(originalText);
        
        assertNotNull(encryptedText);
        assertNotEquals(originalText, encryptedText);
        
        String decryptedText = encryptionUtil.decrypt(encryptedText);
        assertEquals(originalText, decryptedText);
    }

    @Test
    void testEncryptWithDifferentKeyLengths() {
        String originalText = "Secret message";
        
        // 24 bytes
        ReflectionTestUtils.setField(encryptionUtil, "encryptionKey", VALID_KEY_24);
        String encrypted24 = encryptionUtil.encrypt(originalText);
        assertEquals(originalText, encryptionUtil.decrypt(encrypted24));
        
        // 32 bytes
        ReflectionTestUtils.setField(encryptionUtil, "encryptionKey", VALID_KEY_32);
        String encrypted32 = encryptionUtil.encrypt(originalText);
        assertEquals(originalText, encryptionUtil.decrypt(encrypted32));
    }

    @Test
    void testEncryptNullThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> encryptionUtil.encrypt(null));
    }

    @Test
    void testDecryptNullOrEmptyThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> encryptionUtil.decrypt(null));
        assertThrows(IllegalArgumentException.class, () -> encryptionUtil.decrypt(""));
    }

    @Test
    void testInvalidKeyLengthThrowsException() {
        ReflectionTestUtils.setField(encryptionUtil, "encryptionKey", "short");
        assertThrows(IllegalArgumentException.class, () -> encryptionUtil.encrypt("text"));
        assertThrows(IllegalArgumentException.class, () -> encryptionUtil.decrypt("text"));
    }

    @Test
    void testDecryptCorruptedDataThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> encryptionUtil.decrypt("not-base64-encoded"));
        assertThrows(IllegalArgumentException.class, () -> encryptionUtil.decrypt("YW55IGNhcm5hbCBwbGVhc3VyZS4=")); // Valid base64 but not encrypted data
    }
    
    @Test
    void testDecryptWithWrongKeyThrowsException() {
        String originalText = "Secret message";
        String encryptedText = encryptionUtil.encrypt(originalText);
        
        ReflectionTestUtils.setField(encryptionUtil, "encryptionKey", "anotherKey16byte");
        assertThrows(IllegalArgumentException.class, () -> encryptionUtil.decrypt(encryptedText));
    }
}
