package io.tomrss.gluon.core.spec;

public record FieldSpec(
        String name,
        Class<?> type, // TODO having class here and not string restricts only to Java!
        boolean nullable,
        boolean unique,
        int length
) {
    public FieldSpec(String name, Class<?> type) {
        this(name, type, true, false, 0);
    }

    public FieldSpec(String name, Class<?> type, boolean nullable, boolean unique) {
        this(name, type, nullable, unique, 0);
    }
}
