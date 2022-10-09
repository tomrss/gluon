package io.tomrss.gluon.core.model;

public class Relation {
    Relation() {
    }

    String fieldName;
    Entity targetEntity;
    RelationType type;
    String joinColumn;
    String inverseJoinColumn;
    String foreignKeyName;
    boolean nullable;
    boolean unique;

    public String getFieldName() {
        return fieldName;
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
