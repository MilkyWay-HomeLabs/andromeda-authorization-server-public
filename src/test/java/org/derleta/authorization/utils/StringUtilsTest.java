package org.derleta.authorization.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StringUtilsTest {

    @Test
    void testNullToEmpty_WithNull() {
        assertEquals("", StringUtils.nullToEmpty(null));
    }

    @Test
    void testNullToEmpty_WithEmptyString() {
        assertEquals("", StringUtils.nullToEmpty(""));
    }

    @Test
    void testNullToEmpty_WithRegularString() {
        assertEquals("test", StringUtils.nullToEmpty("test"));
    }
}
