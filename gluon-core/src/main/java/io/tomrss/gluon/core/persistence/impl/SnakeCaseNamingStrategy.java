package io.tomrss.gluon.core.persistence.impl;

import io.tomrss.gluon.core.persistence.PhysicalNamingStrategy;
import io.tomrss.gluon.core.spec.EntitySpec;
import io.tomrss.gluon.core.spec.FieldSpec;
import io.tomrss.gluon.core.spec.IndexSpec;
import io.tomrss.gluon.core.spec.RelationSpec;
import io.tomrss.gluon.core.util.CaseUtils;

public class SnakeCaseNamingStrategy implements PhysicalNamingStrategy {

    @Override
    public String table(EntitySpec entity) {
        return CaseUtils.toSnakeCase(entity.name);
    }

    @Override
    public String sequence(EntitySpec entity) {
        return table(entity) + "_seq";
    }

    @Override
    public String sequenceGenerator(EntitySpec entity) {
        return sequence(entity) + "_gen";
    }

    @Override
    public String primaryKey(EntitySpec entity) {
        return table(entity) + "_pk";
    }

    @Override
    public String column(FieldSpec field) {
        return CaseUtils.toSnakeCase(field.name);
    }

    @Override
    public String foreignKey(RelationSpec relation) {
        return CaseUtils.toSnakeCase(relation.name) + "_fk";
    }

    @Override
    public String inverseJoinColumn(EntitySpec entitySpec, RelationSpec relationSpec) {
        return table(entitySpec) + "_id";
    }

    @Override
    public String joinTable(EntitySpec entitySpec, RelationSpec relationSpec) {
        return table(entitySpec) + "_" + table(relationSpec.targetEntity);
    }

    @Override
    public String index(EntitySpec entitySpec, IndexSpec index) {
        return "idx_" + table(entitySpec) + "_" + index.name;
    }

    @Override
    public String joinColumn(RelationSpec relation) {
        return CaseUtils.toSnakeCase(relation.name) + "_id";
    }
}
