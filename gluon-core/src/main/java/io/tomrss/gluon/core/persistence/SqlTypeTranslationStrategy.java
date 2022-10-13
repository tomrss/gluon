package io.tomrss.gluon.core.persistence;

import io.tomrss.gluon.core.spec.FieldSpec;

@FunctionalInterface
public interface SqlTypeTranslationStrategy {
    String sqlType(FieldSpec field);
}
