package io.tomrss.gluon.core.spec.impl;

import io.tomrss.gluon.core.spec.EntitySpec;
import io.tomrss.gluon.core.spec.EntitySpecReader;

import java.io.IOException;
import java.util.List;

public class MockEntitySpecReader implements EntitySpecReader {

    private final List<EntitySpec> entitySpecs;

    public MockEntitySpecReader(List<EntitySpec> entitySpecs) {
        this.entitySpecs = entitySpecs;
    }

    @Override
    public List<EntitySpec> read() throws IOException {
        return entitySpecs;
    }
}
