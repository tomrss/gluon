package io.tomrss.gluon.core.persistence.impl;

import io.tomrss.gluon.core.persistence.SqlTypeTranslationStrategy;
import io.tomrss.gluon.core.spec.FieldSpec;

public class H2TypeTranslationStrategy implements SqlTypeTranslationStrategy {
    // FIXME is probably wrong
    @Override
    public String sqlType(FieldSpec field) {
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
