package io.tomrss.gluon.core.util;

public class CaseUtils {
    public static String toSnakeCase(String s) {
        return s.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
    }

    public static String toHyphenSeparated(String s) {
        return s.replaceAll("([a-z])([A-Z])", "$1-$2").toLowerCase();
    }
}
