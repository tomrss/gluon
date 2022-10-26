package io.tomrss.gluon.core.util;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Utilities related to words.
 *
 * @author Tommaso Rossi
 */
public class WordUtils {

    public static final String CAMEL_CASE_PATTERN = "([a-z])([A-Z])";
    public static final String HYPHENATED_PATTERN = "([a-z])-([a-z])";

    private WordUtils() {
    }

    /**
     * Convert string from camel case to snake case.
     *
     * @param str camel case string
     * @return snake case string
     */
    public static String camelCaseToSnakeCase(String str) {
        if (str == null) {
            return null;
        }
        return str.replaceAll(CAMEL_CASE_PATTERN, "$1_$2").toLowerCase();
    }

    /**
     * Convert string from camel case to snake case, but all upper case.
     *
     * @param str camel case string
     * @return snake case string, but upper case
     */
    public static String camelCaseToSnakeCaseUpperCase(String str) {
        if (str == null) {
            return null;
        }
        return str.replaceAll(CAMEL_CASE_PATTERN, "$1_$2").toUpperCase();
    }

    /**
     * Convert string from camel case to hyphenated, that is hyphen separated.
     *
     * @param str camel case string
     * @return hyphenated string
     */
    public static String camelCaseToHyphenated(String str) {
        if (str == null) {
            return null;
        }
        return str.replaceAll(CAMEL_CASE_PATTERN, "$1-$2").toLowerCase();
    }

    /**
     * Capitalize first letter of string.
     *
     * @param str string
     * @return capitalized
     */
    public static String capitalize(String str) {
        if (str == null) {
            return null;
        }
        if (str.length() == 0) {
            return str;
        }
        if (str.length() == 1) {
            return str.toUpperCase();
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    /**
     * Convert hyphenated string to descriptive string (with spaces and capitals).
     *
     * @param str hyphenated string
     * @return descriptive string
     */
    public static String hyphenatedToDescriptive(String str) {
        return Arrays.stream(str.replaceAll(HYPHENATED_PATTERN, "$1 $2").split("\\s"))
                .map(WordUtils::capitalize)
                .collect(Collectors.joining(" "));
    }
}
