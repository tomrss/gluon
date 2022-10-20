package io.tomrss.gluon.core.spec.impl;

import io.tomrss.gluon.core.spec.EntitySpec;
import io.tomrss.gluon.core.spec.EntitySpecLoader;

import java.io.IOException;
import java.util.List;

public class MockEntitySpecLoader implements EntitySpecLoader {

    private final List<EntitySpec> entitySpecs;

    public MockEntitySpecLoader(List<EntitySpec> entitySpecs) {
        this.entitySpecs = entitySpecs;
    }

    @Override
    public List<EntitySpec> load() throws IOException {
        return entitySpecs;
    }
}
