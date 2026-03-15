package org.derleta.authorization.utils;

import org.junit.jupiter.api.Test;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ValidatorUtilsTest {

    private final Set<String> allowedSortColumns = Set.of("username", "email", "id");
    private final Set<String> allowedSortOrders = Set.of("ASC", "DESC");

    @Test
    void testValidateSortParameters_Success() {
        assertDoesNotThrow(() -> 
            ValidatorUtils.validateSortParameters("username", "ASC", allowedSortColumns, allowedSortOrders)
        );
        assertDoesNotThrow(() -> 
            ValidatorUtils.validateSortParameters("email", "desc", allowedSortColumns, allowedSortOrders)
        );
    }

    @Test
    void testValidateSortParameters_InvalidSortOrder() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> 
            ValidatorUtils.validateSortParameters("username", "INVALID", allowedSortColumns, allowedSortOrders)
        );
        assertEquals("Invalid sortOrder parameter: INVALID", exception.getMessage());
    }

    @Test
    void testValidateSortParameters_InvalidSortBy() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> 
            ValidatorUtils.validateSortParameters("password", "ASC", allowedSortColumns, allowedSortOrders)
        );
        assertEquals("Invalid sortBy parameter: password", exception.getMessage());
    }
}
