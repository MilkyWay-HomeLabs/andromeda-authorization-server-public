package org.derleta.authorization.utils;

public final class StringUtils {

    private StringUtils() {
    }

    public static String nullToEmpty(final String value) {
        return value == null ? "" : value;
    }

}
