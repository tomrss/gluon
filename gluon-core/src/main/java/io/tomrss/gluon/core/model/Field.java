package io.tomrss.gluon.core.model;

public class Field {

    Field() {
    }

    String name;
    Class<?> type;
    boolean nullable;
    boolean unique;
    int length;
    String column;
    String sqlType;

    public String getName() {
        return name;
    }

    public Class<?> getType() {
        return type;
    }

    public boolean isNullable() {
        return nullable;
    }

    public boolean isUnique() {
        return unique;
    }

    public String getColumn() {
        return column;
    }

    public String getSqlType() {
        return sqlType;
    }

    public int getLength() {
        return length;
    }
}
