package io.tomrss.gluon.core.spec;

import io.tomrss.gluon.core.model.RelationType;

public record RelationSpec(
        String name,
        String targetEntity,
        RelationType type,
        boolean nullable,
        boolean unique
) {
}
