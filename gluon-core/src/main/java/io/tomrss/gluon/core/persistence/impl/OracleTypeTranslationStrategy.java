package io.tomrss.gluon.core.persistence.impl;

import io.tomrss.gluon.core.persistence.SqlTypeTranslationStrategy;
import io.tomrss.gluon.core.spec.FieldSpec;

public class OracleTypeTranslationStrategy implements SqlTypeTranslationStrategy {
    // FIXME is probably wrong
    @Override
    public String sqlType(FieldSpec field) {
        // FIXME this is ugly and dangerous... but we don't want to rewrite hibernate and we don't want to have it as dependency either
        if (field.type() == String.class) return "VARCHAR2(" + field.length() + ")";
        if (Number.class.isAssignableFrom(field.type())) return "NUMBER";
        if (field.type() == Boolean.class) return "NUMBER(1)";
        // TODO other, like dates...

        throw new IllegalArgumentException("Type " + field.type() + " not supported as sql type");
    }
}
