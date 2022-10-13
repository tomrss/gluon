package io.tomrss.gluon.core.model;

public class Relation {
    Relation() {
    }

    String name;
    Entity targetEntity;
    RelationType type;
    String joinColumn;
    String inverseJoinColumn;
    String joinTable;
    String foreignKeyName;
    boolean nullable;
    boolean unique;

    public String getName() {
        return name;
    }

    public Entity getTargetEntity() {
        return targetEntity;
    }

    public RelationType getType() {
        return type;
    }

    public String getJoinColumn() {
        return joinColumn;
    }

    public String getInverseJoinColumn() {
        return inverseJoinColumn;
    }

    public String getJoinTable() {
        return joinTable;
    }

    public String getForeignKeyName() {
        return foreignKeyName;
    }

    public boolean isNullable() {
        return nullable;
    }

    public boolean isUnique() {
        return unique;
    }
}
