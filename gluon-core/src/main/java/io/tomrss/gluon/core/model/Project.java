package io.tomrss.gluon.core.model;

import io.tomrss.gluon.core.persistence.DatabaseVendor;

public class Project {

    String groupId;
    String artifactId;
    String version;
    String friendlyName;
    String description;
    String basePackage;
    String basePackagePath;
    DatabaseVendor dbVendor;

    Project() {
    }

    public String getGroupId() {
        return groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public String getVersion() {
        return version;
    }

    public String getFriendlyName() {
        return friendlyName;
    }

    public String getDescription() {
        return description;
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
