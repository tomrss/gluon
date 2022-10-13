package io.tomrss.gluon.core.persistence;

import io.tomrss.gluon.core.spec.EntitySpec;
import io.tomrss.gluon.core.spec.FieldSpec;
import io.tomrss.gluon.core.spec.IndexSpec;
import io.tomrss.gluon.core.spec.RelationSpec;
import io.tomrss.gluon.core.util.CaseUtils;

public interface PhysicalNamingStrategy {
    String table(EntitySpec entity);

    String sequence(EntitySpec entity);

    String sequenceGenerator(EntitySpec entity);

    String primaryKey(EntitySpec entity);

    String column(FieldSpec field);

    String foreignKey(RelationSpec relation);

    String joinColumn(RelationSpec relation);

    String inverseJoinColumn(EntitySpec entitySpec, RelationSpec relationSpec);

    String joinTable(EntitySpec entitySpec, RelationSpec relationSpec);

    String index(EntitySpec entitySpec, IndexSpec index);

    default String resourcePath(EntitySpec entity) {
        return CaseUtils.toHyphenSeparated(entity.name);
    }
}
