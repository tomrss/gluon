package io.tomrss.gluon.core.spec;

public class FieldSpec {
    public String name;
    public Class<?> type;
    public boolean nullable = true;
    public boolean unique = false;
    public int length = 255;

    public FieldSpec() {
    }

    public FieldSpec(String name, Class<?> type) {
        this(name, type, true, false, 255);
    }

    public FieldSpec(String name, Class<?> type, boolean nullable, boolean unique) {
        this(name, type, nullable, unique, 255);
    }

    public FieldSpec(String name, Class<?> type, boolean nullable, boolean unique, int length) {
        this.name = name;
        this.type = type;
        this.nullable = nullable;
        this.unique = unique;
        this.length = length;
    }
}
