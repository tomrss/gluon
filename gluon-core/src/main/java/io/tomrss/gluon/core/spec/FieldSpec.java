package io.tomrss.gluon.core.spec;

public record FieldSpec(
        String name,
        Class<?> type,
        boolean nullable,
        boolean unique,
        int length
) {
    public FieldSpec(String name, Class<?> type) {
        this(name, type, true, false, 255);
    }

    public FieldSpec(String name, Class<?> type, boolean nullable, boolean unique) {
        this(name, type, nullable, unique, 255);
    }
}
