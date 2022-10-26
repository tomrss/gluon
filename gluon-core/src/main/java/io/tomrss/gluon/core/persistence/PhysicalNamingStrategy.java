package io.tomrss.gluon.core.persistence;

import io.tomrss.gluon.core.util.WordUtils;

public interface PhysicalNamingStrategy {

    String table(String entityName);

    String sequence(String entityName);

    String sequenceGenerator(String entityName);

    String primaryKey(String entityName);

    String column(String fieldName);

    String foreignKey(String relationName);

    String inverseJoinColumn(String entityName);

    String joinTable(String firstEntityName, String secondEntityName);

    String index(String entityName, String indexName);

    String joinColumn(String relationName);

    default String resourcePath(String entityName) {
        return WordUtils.camelCaseToHyphenated(entityName);
    }
}
