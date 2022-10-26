package io.tomrss.gluon.core.persistence.impl;

import io.tomrss.gluon.core.persistence.PhysicalNamingStrategy;
import io.tomrss.gluon.core.util.WordUtils;

public class SnakeCaseNamingStrategy implements PhysicalNamingStrategy {

    @Override
    public String table(String entityName) {
        return WordUtils.camelCaseToSnakeCase(entityName);
    }

    @Override
    public String sequence(String entityName) {
        return table(entityName) + "_seq";
    }

    @Override
    public String sequenceGenerator(String entityName) {
        return sequence(entityName) + "_gen";
    }

    @Override
    public String primaryKey(String entityName) {
        return table(entityName) + "_pk";
    }

    @Override
    public String column(String fieldName) {
        return WordUtils.camelCaseToSnakeCase(fieldName);
    }

    @Override
    public String foreignKey(String relationName) {
        return WordUtils.camelCaseToSnakeCase(relationName) + "_fk";
    }

    @Override
    public String inverseJoinColumn(String entityName) {
        return table(entityName) + "_id";
    }

    @Override
    public String joinTable(String firstEntityName, String secondEntityName) {
        return table(firstEntityName) + "_" + table(secondEntityName);
    }

    @Override
    public String index(String entityName, String indexName) {
        return "idx_" + table(entityName) + "_" + indexName;
    }

    @Override
    public String joinColumn(String relationName) {
        return WordUtils.camelCaseToSnakeCase(relationName) + "_id";
    }
}
