package io.tomrss.gluon.core.spec;

import io.tomrss.gluon.core.persistence.DatabaseVendor;

public class ProjectSpec {
    public String groupId;
    public String artifactId;
    public String basePackage;
    public DatabaseVendor databaseVendor;

    public ProjectSpec(String groupId, String artifactId, String basePackage, DatabaseVendor databaseVendor) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.basePackage = basePackage;
        this.databaseVendor = databaseVendor;
    }
}
