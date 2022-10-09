package io.tomrss.gluon.core.strategy;

import io.tomrss.gluon.core.model.config.FieldConfig;

@FunctionalInterface
public interface SqlTypeTranslationStrategy {
    String sqlType(FieldConfig field);
}
