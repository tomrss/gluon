package io.tomrss.gluon.core.persistence.impl;

import io.tomrss.gluon.core.persistence.PhysicalNamingStrategy;
import io.tomrss.gluon.core.spec.EntitySpec;
import io.tomrss.gluon.core.spec.FieldSpec;
import io.tomrss.gluon.core.spec.IndexSpec;
import io.tomrss.gluon.core.spec.RelationSpec;
import io.tomrss.gluon.core.util.CaseUtils;

public class SnakeCaseUpperCaseNamingStrategy implements PhysicalNamingStrategy {

    @Override
    public String table(EntitySpec entity) {
        return CaseUtils.toSnakeCaseUpperCase(entity.name);
    }

    @Override
    public String sequence(EntitySpec entity) {
        return table(entity) + "_SEQ";
    }

    @Override
    public String sequenceGenerator(EntitySpec entity) {
        return sequence(entity) + "_GEN";
    }

    @Override
    public String primaryKey(EntitySpec entity) {
        return table(entity) + "_PK";
    }

    @Override
    public String column(FieldSpec field) {
        return CaseUtils.toSnakeCaseUpperCase(field.name);
    }

    @Override
    public String foreignKey(RelationSpec relation) {
        return CaseUtils.toSnakeCaseUpperCase(relation.name) + "_FK";
    }

    @Override
    public String inverseJoinColumn(EntitySpec entitySpec, RelationSpec relationSpec) {
        return table(entitySpec) + "_ID";
    }

    @Override
    public String joinTable(EntitySpec entitySpec, RelationSpec relationSpec) {
        return table(entitySpec) + "_" + table(relationSpec.targetEntity);
    }

    @Override
    public String index(EntitySpec entitySpec, IndexSpec index) {
        return "IDX_" + table(entitySpec) + "_" + index.name;
    }

    @Override
    public String joinColumn(RelationSpec relation) {
        return CaseUtils.toSnakeCaseUpperCase(relation.name) + "_ID";
    }
}
