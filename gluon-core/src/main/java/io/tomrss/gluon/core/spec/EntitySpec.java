package io.tomrss.gluon.core.spec;

import java.util.Collections;
import java.util.List;

public record EntitySpec(
        String name,
        List<FieldSpec> fields,
        List<RelationSpec> relations,
        List<IndexSpec> indexes
) {
    public EntitySpec(String name, List<FieldSpec> fields) {
        this(name, fields, Collections.emptyList());
    }

    public EntitySpec(String name, List<FieldSpec> fields, List<RelationSpec> relations) {
        this(name, fields, relations, Collections.emptyList());
    }
}
