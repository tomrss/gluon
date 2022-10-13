package io.tomrss.gluon.core.spec;

import io.tomrss.gluon.core.model.RelationType;

public class RelationSpec {
    public String name;
    public EntitySpec targetEntity;
    public RelationType type;
    public boolean nullable = true;
    public boolean unique = false;

    public RelationSpec(String name, EntitySpec targetEntity, RelationType type, boolean nullable, boolean unique) {
        this.name = name;
        this.targetEntity = targetEntity;
        this.type = type;
        this.nullable = nullable;
        this.unique = unique;
    }
    public RelationSpec(String name, EntitySpec targetEntity, RelationType type) {
        this(name, targetEntity, type, true, false);

    }
}
