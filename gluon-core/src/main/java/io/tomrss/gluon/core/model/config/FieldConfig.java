package io.tomrss.gluon.core.model.config;

public class FieldConfig {
    public String name;
    public Class<?> type;
    public boolean nullable = true;
    public boolean unique = false;
    public int length = 255;

    public FieldConfig() {
    }

    public FieldConfig(String name, Class<?> type) {
        this(name, type, true, false, 255);
    }

    public FieldConfig(String name, Class<?> type, boolean nullable, boolean unique) {
        this(name, type, nullable, unique, 255);
    }

    public FieldConfig(String name, Class<?> type, boolean nullable, boolean unique, int length) {
        this.name = name;
        this.type = type;
        this.nullable = nullable;
        this.unique = unique;
        this.length = length;
    }
}
