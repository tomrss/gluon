package io.tomrss.gluon.core.spec;

import io.tomrss.gluon.core.persistence.DatabaseVendor;

public record ProjectSpec(
        String groupId,
        String artifactId,
        String version,
        String basePackage,
        DatabaseVendor databaseVendor
) {
}
