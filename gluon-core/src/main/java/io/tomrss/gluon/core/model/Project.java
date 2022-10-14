package io.tomrss.gluon.core.model;

import io.tomrss.gluon.core.persistence.DatabaseVendor;

public class Project {
    Project() {
    }


    String groupId;
    String artifactId;
    String version;
    String basePackage;
    String basePackagePath;
    DatabaseVendor dbVendor;

    public String getGroupId() {
        return groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public String getVersion() {
        return version;
    }

    public String getBasePackage() {
        return basePackage;
    }

    public String getBasePackagePath() {
        return basePackagePath;
    }

    public DatabaseVendor getDbVendor() {
        return dbVendor;
    }
}
