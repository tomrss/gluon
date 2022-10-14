package io.tomrss.gluon.mavenplugin;

import io.tomrss.gluon.core.GluonBuilder;
import io.tomrss.gluon.core.persistence.DatabaseVendor;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;

@Mojo(name = "create", requiresProject = false)
public class CreateProjectMojo extends AbstractMojo {

    @Parameter(property = "projectGroupId")
    private String projectGroupId;

    @Parameter(property = "projectArtifactId")
    private String projectArtifactId;

    @Parameter(property = "projectVersion")
    private String projectVersion;

    @Parameter(property = "basePackage")
    private String basePackage;

    @Parameter(property = "templateDirectory")
    private String templateDirectory;

    @Parameter(property = "generationDirectory")
    private String generationDirectory;

    @Parameter(property = "rawFilesDirectory")
    private String rawFilesDirectory;

    @Parameter(property = "databaseVendor")
    private String databaseVendor;

    @Parameter(property = "templateExtension")
    private String templateExtension;

    @Parameter(property = "entitiesDirectory")
    private String entitiesDirectory;

    public void execute() throws MojoExecutionException {
        try {
            final GluonBuilder gluonBuilder = new GluonBuilder();

            Optional.ofNullable(templateDirectory).map(Paths::get).ifPresent(gluonBuilder::templateDirectory);
            Optional.ofNullable(rawFilesDirectory).map(Paths::get).ifPresent(gluonBuilder::rawFilesDirectory);
            Optional.ofNullable(generationDirectory).map(Paths::get).ifPresent(gluonBuilder::generationDirectory);
            Optional.ofNullable(projectGroupId).ifPresent(gluonBuilder::groupId);
            Optional.ofNullable(projectArtifactId).ifPresent(gluonBuilder::artifactId);
            Optional.ofNullable(basePackage).ifPresent(gluonBuilder::basePackage);
            Optional.ofNullable(databaseVendor).map(DatabaseVendor::valueOf).ifPresent(gluonBuilder::databaseVendor);
            Optional.ofNullable(templateExtension).ifPresent(gluonBuilder::templateExtension);
            Optional.ofNullable(entitiesDirectory).map(Paths::get).ifPresent(gluonBuilder::readEntitiesFromJson);

            gluonBuilder.createGluon().generateProject();
        } catch (IOException e) {
            throw new MojoExecutionException("Unable to generate project", e);
        }
    }
}
