package io.tomrss.gluon.core.model.config;

import io.tomrss.gluon.core.model.RelationType;

public class RelationConfig {
    public String name;
    public EntityConfig targetEntity;
    public RelationType type;
    public boolean nullable = true;
    public boolean unique = false;

    public RelationConfig(String name, EntityConfig targetEntity, RelationType type, boolean nullable, boolean unique) {
        this.name = name;
        this.targetEntity = targetEntity;
        this.type = type;
        this.nullable = nullable;
        this.unique = unique;
    }
    public RelationConfig(String name, EntityConfig targetEntity, RelationType type) {
        this(name, targetEntity, type, true, false);

    }
}
