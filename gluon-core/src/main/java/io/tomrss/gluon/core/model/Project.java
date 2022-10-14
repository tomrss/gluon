package io.tomrss.gluon.core.model;

import io.tomrss.gluon.core.persistence.DatabaseVendor;

public class Project {
    Project() {
    }


    String groupId;
    String artifactId;
    String basePackage;
    DatabaseVendor dbVendor;

    public String getGroupId() {
        return groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public String getBasePackage() {
        return basePackage;
    }

    public DatabaseVendor getDbVendor() {
        return dbVendor;
    }
}
