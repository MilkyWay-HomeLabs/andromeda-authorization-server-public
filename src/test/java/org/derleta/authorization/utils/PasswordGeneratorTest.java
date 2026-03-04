package org.derleta.authorization.utils;

import org.junit.jupiter.api.Test;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class PasswordGeneratorTest {

    @Test
    void testGenerateStrongPassword_DefaultLength() {
        String password = PasswordGenerator.generateStrongPassword();
        assertEquals(12, password.length());
        assertPasswordComposition(password);
    }

    @Test
    void testGenerateStrongPassword_CustomLength() {
        int length = 16;
        String password = PasswordGenerator.generateStrongPassword(length);
        assertEquals(length, password.length());
        assertPasswordComposition(password);
    }

    @Test
    void testGenerateStrongPassword_MinimumLength() {
        // The implementation starts with 4 specific characters and then loops from 4 to length.
        // If length is < 4, it might still return 4 or behave oddly, but 4 is the logical minimum for all types.
        int length = 4;
        String password = PasswordGenerator.generateStrongPassword(length);
        assertEquals(4, password.length());
        assertPasswordComposition(password);
    }

    @Test
    void testGenerateStrongPassword_Randomness() {
        Set<String> passwords = new HashSet<>();
        for (int i = 0; i < 100; i++) {
            passwords.add(PasswordGenerator.generateStrongPassword());
        }
        // Very high probability that 100 generated passwords are unique.
        assertEquals(100, passwords.size());
    }

    private void assertPasswordComposition(String password) {
        boolean hasUpper = false;
        boolean hasLower = false;
        boolean hasDigit = false;
        boolean hasSpecial = false;

        String upperCaseChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowerCaseChars = "abcdefghijklmnopqrstuvwxyz";
        String digits = "0123456789";
        String specialChars = "!@#$%^&*()-_=+";

        for (char c : password.toCharArray()) {
            if (upperCaseChars.indexOf(c) >= 0) hasUpper = true;
            else if (lowerCaseChars.indexOf(c) >= 0) hasLower = true;
            else if (digits.indexOf(c) >= 0) hasDigit = true;
            else if (specialChars.indexOf(c) >= 0) hasSpecial = true;
        }

        assertTrue(hasUpper, "Password should contain at least one uppercase letter: " + password);
        assertTrue(hasLower, "Password should contain at least one lowercase letter: " + password);
        assertTrue(hasDigit, "Password should contain at least one digit: " + password);
        assertTrue(hasSpecial, "Password should contain at least one special character: " + password);
    }
}
