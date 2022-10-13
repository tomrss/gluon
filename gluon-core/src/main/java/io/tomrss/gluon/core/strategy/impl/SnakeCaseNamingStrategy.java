package io.tomrss.gluon.core.strategy.impl;

import io.tomrss.gluon.core.model.Index;
import io.tomrss.gluon.core.model.config.IndexConfig;
import io.tomrss.gluon.core.model.config.RelationConfig;
import io.tomrss.gluon.core.util.CaseUtils;
import io.tomrss.gluon.core.model.config.EntityConfig;
import io.tomrss.gluon.core.model.config.FieldConfig;
import io.tomrss.gluon.core.strategy.PhysicalNamingStrategy;

public class SnakeCaseNamingStrategy implements PhysicalNamingStrategy {

    @Override
    public String table(EntityConfig entity) {
        return CaseUtils.toSnakeCase(entity.name);
    }

    @Override
    public String sequence(EntityConfig entity) {
        return table(entity) + "_seq";
    }

    @Override
    public String sequenceGenerator(EntityConfig entity) {
        return sequence(entity) + "_gen";
    }

    @Override
    public String primaryKey(EntityConfig entity) {
        return table(entity) + "_pk";
    }

    @Override
    public String column(FieldConfig field) {
        return CaseUtils.toSnakeCase(field.name);
    }

    @Override
    public String foreignKey(RelationConfig relation) {
        return CaseUtils.toSnakeCase(relation.name) + "_fk";
    }

    @Override
    public String inverseJoinColumn(EntityConfig entityConfig, RelationConfig relationConfig) {
        return table(entityConfig) + "_id";
    }

    @Override
    public String joinTable(EntityConfig entityConfig, RelationConfig relationConfig) {
        return table(entityConfig) + "_" + table(relationConfig.targetEntity);
    }

    @Override
    public String index(EntityConfig entityConfig, IndexConfig index) {
        return "idx_" + table(entityConfig) + "_" + index.name;
    }

    @Override
    public String joinColumn(RelationConfig relation) {
        return CaseUtils.toSnakeCase(relation.name) + "_id";
    }
}
