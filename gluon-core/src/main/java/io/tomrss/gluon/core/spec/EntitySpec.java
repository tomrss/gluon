package io.tomrss.gluon.core.spec;

import java.util.Collections;
import java.util.List;

public class EntitySpec {
    public String name;
    public List<FieldSpec> fields;
    public List<RelationSpec> relations;
    public List<IndexSpec> indexes;

    public EntitySpec(String name, List<FieldSpec> fields) {
        this(name, fields, Collections.emptyList());
    }

    public EntitySpec(String name, List<FieldSpec> fields, List<RelationSpec> relations) {
        this(name, fields, relations, Collections.emptyList());
    }

    public EntitySpec(String name, List<FieldSpec> fields, List<RelationSpec> relations, List<IndexSpec> indexes) {
        this.name = name;
        this.fields = fields;
        this.relations = relations;
        this.indexes = indexes;
    }
}
