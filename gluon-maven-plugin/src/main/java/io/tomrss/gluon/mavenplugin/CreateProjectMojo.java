package io.tomrss.gluon.mavenplugin;

import io.tomrss.gluon.core.GluonBuilder;
import io.tomrss.gluon.core.persistence.DatabaseVendor;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

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

    @Parameter(property = "projectFriendlyName")
    private String projectFriendlyName;

    @Parameter(property = "projectDescription")
    private String projectDescription;

    @Parameter(property = "basePackage")
    private String basePackage;

    @Parameter(property = "customTemplates")
    private String customTemplates;

    @Parameter(property = "projectDirectory")
    private String projectDirectory;

    @Parameter(property = "imageRegistry")
    private String imageRegistry;

    @Parameter(property = "databaseVendor")
    private String databaseVendor;

    @Parameter(property = "templateExtension")
    private String templateExtension;

    @Parameter(property = "entities")
    private String entities;

    @Parameter(property = "archetype")
    private String archetype;

    public void execute() throws MojoExecutionException {
        try {
            final GluonBuilder gluonBuilder = new GluonBuilder();

            // all this optional hassle means: let gluon builder set the defaults, don't bother here
            Optional.ofNullable(customTemplates).map(Paths::get).ifPresent(gluonBuilder::customTemplates);
            Optional.ofNullable(projectDirectory).map(Paths::get).ifPresent(gluonBuilder::projectDirectory);
            Optional.ofNullable(projectGroupId).ifPresent(gluonBuilder::groupId);
            Optional.ofNullable(projectArtifactId).ifPresent(gluonBuilder::artifactId);
            Optional.ofNullable(projectVersion).ifPresent(gluonBuilder::version);
            Optional.ofNullable(projectFriendlyName).ifPresent(gluonBuilder::friendlyName);
            Optional.ofNullable(projectDescription).ifPresent(gluonBuilder::description);
            Optional.ofNullable(basePackage).ifPresent(gluonBuilder::basePackage);
            Optional.ofNullable(imageRegistry).ifPresent(gluonBuilder::imageRegistry);
            Optional.ofNullable(databaseVendor).map(DatabaseVendor::valueOf).ifPresent(gluonBuilder::databaseVendor);
            Optional.ofNullable(templateExtension).ifPresent(gluonBuilder::templateExtension);
            Optional.ofNullable(entities).map(Paths::get).ifPresent(gluonBuilder::readEntitiesFromJson);
            Optional.ofNullable(archetype).ifPresent(gluonBuilder::archetype);

            gluonBuilder.createGluon().generateProject();
        } catch (Exception e) {
            throw new MojoExecutionException("Unable to generate project", e);
        }
    }
}
