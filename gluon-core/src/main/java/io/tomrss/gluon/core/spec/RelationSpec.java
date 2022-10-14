package io.tomrss.gluon.core.spec;

import io.tomrss.gluon.core.model.RelationType;

public record RelationSpec(
        String name,
        EntitySpec targetEntity,
        RelationType type,
        boolean nullable,
        boolean unique
) {
}
