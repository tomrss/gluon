package io.tomrss.gluon.core.util;

import java.util.Arrays;
import java.util.stream.Collectors;

public class CaseUtils {

    public static final String CAMEL_CASE_PATTERN = "([a-z])([A-Z])";
    public static final String HYPHEN_SEPARATED_PATTERN = "([a-z])-([a-z])";

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

    public static String hyphenSeparatedToDescriptive(String s) {
        return Arrays.stream(s.replaceAll(HYPHEN_SEPARATED_PATTERN, "$1 $2").split("\\s"))
                .map(CaseUtils::capitalize)
                .collect(Collectors.joining(" "));
    }
}
