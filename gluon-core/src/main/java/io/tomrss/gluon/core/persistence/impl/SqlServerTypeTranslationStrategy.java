package io.tomrss.gluon.core.persistence.impl;

import io.tomrss.gluon.core.persistence.SqlTypeTranslationStrategy;
import io.tomrss.gluon.core.spec.FieldSpec;

public class SqlServerTypeTranslationStrategy implements SqlTypeTranslationStrategy {
    // FIXME is probably wrong
    @Override
    public String sqlType(FieldSpec field) {
        // FIXME this is ugly and dangerous... but we don't want to rewrite hibernate and we don't want to have it as dependency either
        if (field.type == String.class) return "VARCHAR(" + field.length + ")";
        if (field.type == Long.class) return "BIGINT";
        if (field.type == Integer.class) return "INT";
        if (field.type == Short.class) return "SMALLINT";
        if (field.type == Boolean.class) return "BOOLEAN";
        // TODO other, like dates...

        throw new IllegalArgumentException("Type " + field.type + " not supported as sql type");
    }
}
