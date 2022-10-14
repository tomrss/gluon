package io.tomrss.gluon.core.util;

public class CaseUtils {
    public static String toSnakeCase(String s) {
        if (s == null) {
            return null;
        }
        return s.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
    }

    public static String toSnakeCaseUpperCase(String s) {
        if (s == null) {
            return null;
        }
        return s.replaceAll("([a-z])([A-Z])", "$1_$2").toUpperCase();
    }

    public static String toHyphenSeparated(String s) {
        if (s == null) {
            return null;
        }
        return s.replaceAll("([a-z])([A-Z])", "$1-$2").toLowerCase();
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
