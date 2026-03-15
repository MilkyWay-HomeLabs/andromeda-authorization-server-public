package org.derleta.authorization.domain.entity;

import org.junit.jupiter.api.Test;
import java.sql.Timestamp;
import static org.junit.jupiter.api.Assertions.*;

class UserEntityDecryptedTest {

    @Test
    void testConstructorWithUserEntity() {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        UserEntity user = new UserEntity(1L, "testuser", "test@example.com", "encrypted", now, now, true, false, 1);
        UserEntityDecrypted decrypted = new UserEntityDecrypted(user, "decrypted");
        
        assertEquals(1L, decrypted.getUserId());
        assertEquals("testuser", decrypted.getUsername());
        assertEquals("decrypted", decrypted.getDecryptedPassword());
        assertEquals("encrypted", decrypted.getPassword());
    }

    @Test
    void testConstructorWithOnlyPassword() {
        UserEntityDecrypted decrypted = new UserEntityDecrypted("decrypted");
        assertEquals("decrypted", decrypted.getDecryptedPassword());
        assertEquals(0, decrypted.getUserId()); // Default long value
    }
}
