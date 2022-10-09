package io.tomrss.gluon.core.model.config;

import java.util.Collections;
import java.util.List;

public class EntityConfig {
    public String name;
    public List<FieldConfig> fields;
    public List<RelationConfig> relations;
    public List<IndexConfig> indexes;

    public EntityConfig(String name, List<FieldConfig> fields) {
        this(name, fields, Collections.emptyList());
    }

    public EntityConfig(String name, List<FieldConfig> fields, List<RelationConfig> relations) {
        this(name, fields, relations, Collections.emptyList());
    }

    public EntityConfig(String name, List<FieldConfig> fields, List<RelationConfig> relations, List<IndexConfig> indexes) {
        this.name = name;
        this.fields = fields;
        this.relations = relations;
        this.indexes = indexes;
    }
}
