package org.derleta.authorization.security.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Component
public class EncryptionUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(EncryptionUtil.class);

    @Value("${encryption.key}")
    private String encryptionKey;

    public String encrypt(String text) {
        if (text == null) {
            throw new IllegalArgumentException("Input text cannot be null");
        }
        try {
            validateKeyLength(encryptionKey);
            Cipher cipher = Cipher.getInstance("AES");
            SecretKeySpec secretKey = new SecretKeySpec(encryptionKey.getBytes(), "AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedBytes = cipher.doFinal(text.getBytes());
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("Encryption failed", e);
            throw new RuntimeException("Encryption failed", e);
        }
    }

    public String decrypt(String text) {
        try {
            if (text == null || text.isEmpty()) throw new IllegalArgumentException("Input text cannot be null or empty");
            validateKeyLength(encryptionKey);
            Cipher cipher = Cipher.getInstance("AES");
            SecretKeySpec secretKey = new SecretKeySpec(encryptionKey.getBytes(), "AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decodedBytes = Base64.getDecoder().decode(text);
            return new String(cipher.doFinal(decodedBytes));
        } catch (Exception e) {
            LOGGER.error("Decryption failed", e);
            throw new IllegalArgumentException("Invalid decryption key or corrupted data", e);
        }
    }

    private void validateKeyLength(String key) {
        int length = key.getBytes().length;
        if (length != 16 && length != 24 && length != 32) {
            throw new IllegalArgumentException("Encryption key must be 16, 24, or 32 bytes long");
        }
    }

}
