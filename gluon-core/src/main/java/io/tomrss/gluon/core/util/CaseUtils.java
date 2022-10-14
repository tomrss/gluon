package io.tomrss.gluon.core.util;

public class CaseUtils {

    public static final String CAMEL_CASE_PATTERN = "([a-z])([A-Z])";

    private CaseUtils() {
    }

    public static String toSnakeCase(String s) {
        if (s == null) {
            return null;
        }
        return s.replaceAll(CAMEL_CASE_PATTERN, "$1_$2").toLowerCase();
    }

    public static String toSnakeCaseUpperCase(String s) {
        if (s == null) {
            return null;
        }
        return s.replaceAll(CAMEL_CASE_PATTERN, "$1_$2").toUpperCase();
    }

    public static String toHyphenSeparated(String s) {
        if (s == null) {
            return null;
        }
        return s.replaceAll(CAMEL_CASE_PATTERN, "$1-$2").toLowerCase();
    }

    public static String capitalize(String s) {
        if (s == null) {
            return null;
        }
        if (s.length() == 0) {
            return s;
        }
        if (s.length() == 1) {
            return s.toUpperCase();
        }
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }
}
