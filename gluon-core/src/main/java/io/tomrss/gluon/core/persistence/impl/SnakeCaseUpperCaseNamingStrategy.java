package io.tomrss.gluon.core.persistence.impl;

import io.tomrss.gluon.core.persistence.PhysicalNamingStrategy;
import io.tomrss.gluon.core.util.WordUtils;

public class SnakeCaseUpperCaseNamingStrategy implements PhysicalNamingStrategy {

    @Override
    public String table(String entityName) {
        return WordUtils.camelCaseToSnakeCaseUpperCase(entityName);
    }

    @Override
    public String sequence(String entityName) {
        return table(entityName) + "_SEQ";
    }

    @Override
    public String sequenceGenerator(String entityName) {
        return sequence(entityName) + "_GEN";
    }

    @Override
    public String primaryKey(String entityName) {
        return table(entityName) + "_PK";
    }

    @Override
    public String column(String fieldName) {
        return WordUtils.camelCaseToSnakeCaseUpperCase(fieldName);
    }

    @Override
    public String foreignKey(String relationName) {
        return WordUtils.camelCaseToSnakeCaseUpperCase(relationName) + "_FK";
    }

    @Override
    public String inverseJoinColumn(String entityName) {
        return table(entityName) + "_ID";
    }

    @Override
    public String joinTable(String firstEntityName, String secondEntityName) {
        return table(firstEntityName) + "_" + table(secondEntityName);
    }

    @Override
    public String index(String entityName, String indexName) {
        return "IDX_" + table(entityName) + "_" + indexName;
    }

    @Override
    public String joinColumn(String relationName) {
        return WordUtils.camelCaseToSnakeCaseUpperCase(relationName) + "_ID";
    }
}
