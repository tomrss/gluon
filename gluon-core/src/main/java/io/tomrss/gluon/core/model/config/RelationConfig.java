package io.tomrss.gluon.core.model.config;

import io.tomrss.gluon.core.model.RelationType;

public class RelationConfig {
    public String fieldName;
    public EntityConfig targetEntity;
    public RelationType type;
    public boolean nullable = true;
    public boolean unique = false;

    public RelationConfig(String fieldName, EntityConfig targetEntity, RelationType type) {
        this.fieldName = fieldName;
        this.targetEntity = targetEntity;
        this.type = type;
    }
}
