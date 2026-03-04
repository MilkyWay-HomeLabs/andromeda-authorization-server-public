package org.derleta.authorization.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ObjectNotSavedExceptionTest {

    @Test
    void testConstructorWithMessage() {
        String message = "Object was not saved";
        ObjectNotSavedException exception = new ObjectNotSavedException(message);

        assertEquals(message, exception.getMessage());
    }

    @Test
    void testConstructorWithNullMessage() {
        ObjectNotSavedException exception = new ObjectNotSavedException(null);

        assertNull(exception.getMessage());
    }
}
