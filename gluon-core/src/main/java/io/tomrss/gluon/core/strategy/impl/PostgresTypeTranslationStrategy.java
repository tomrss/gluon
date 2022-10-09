package io.tomrss.gluon.core.strategy.impl;

import io.tomrss.gluon.core.model.config.FieldConfig;
import io.tomrss.gluon.core.strategy.SqlTypeTranslationStrategy;

public class PostgresTypeTranslationStrategy implements SqlTypeTranslationStrategy {

    @Override
    public String sqlType(FieldConfig field) {
        // FIXME this is ugly and dangerous... but we don't want to rewrite hibernate and we don't want to have it as dependency either
        if (field.type == String.class) return "varchar(" + field.length + ")";
        if (field.type == Long.class) return "bigint";
        if (field.type == Integer.class) return "int";
        if (field.type == Short.class) return "smallint";
        if (field.type == Boolean.class) return "boolean";
        // TODO other, like dates...

        throw new IllegalArgumentException("Type " + field.type + " not supported as sql type");
    }
}
