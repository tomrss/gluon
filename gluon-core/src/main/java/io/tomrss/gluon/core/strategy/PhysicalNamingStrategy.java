package io.tomrss.gluon.core.strategy;

import io.tomrss.gluon.core.model.Index;
import io.tomrss.gluon.core.model.config.IndexConfig;
import io.tomrss.gluon.core.model.config.RelationConfig;
import io.tomrss.gluon.core.util.CaseUtils;
import io.tomrss.gluon.core.model.config.EntityConfig;
import io.tomrss.gluon.core.model.config.FieldConfig;

public interface PhysicalNamingStrategy {
    String table(EntityConfig entity);

    String sequence(EntityConfig entity);

    String sequenceGenerator(EntityConfig entity);

    String primaryKey(EntityConfig entity);

    String column(FieldConfig field);

    String foreignKey(RelationConfig relation);

    String joinColumn(RelationConfig relation);

    String inverseJoinColumn(EntityConfig entityConfig, RelationConfig relationConfig);

    String joinTable(EntityConfig entityConfig, RelationConfig relationConfig);

    String index(EntityConfig entityConfig, IndexConfig index);

    default String resourcePath(EntityConfig entity) {
        return CaseUtils.toHyphenSeparated(entity.name);
    }
}
