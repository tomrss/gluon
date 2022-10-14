package io.tomrss.gluon.core.spec;

import java.util.List;

public record IndexSpec(
        String name,
        List<String> fields,
        boolean unique
) {
    // TODO index could be also on relation...
}
