package io.tomrss.gluon.core.spec;

import java.util.List;

public record IndexSpec(
        String name,
        List<FieldSpec> columns,
        boolean unique
) {
    // TODO index could be also on relation...
}
