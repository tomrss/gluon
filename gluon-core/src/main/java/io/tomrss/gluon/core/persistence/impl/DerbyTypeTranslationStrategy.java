package io.tomrss.gluon.core.persistence.impl;

import io.tomrss.gluon.core.persistence.SqlTypeTranslationStrategy;
import io.tomrss.gluon.core.spec.FieldSpec;

public class DerbyTypeTranslationStrategy implements SqlTypeTranslationStrategy {
    @Override
    public String sqlType(FieldSpec field) {
        throw new UnsupportedOperationException("not implemented");
    }
}
