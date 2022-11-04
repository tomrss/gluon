package io.tomrss.gluon.cli.command;

import io.tomrss.gluon.cli.GluonCommandLine;
import io.tomrss.gluon.core.GluonBuilder;
import io.tomrss.gluon.core.persistence.DatabaseVendor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.nio.file.Paths;
import java.util.Optional;
import java.util.concurrent.Callable;

@Command(name = "create", description = "Create new project")
public class CreateCommand implements Callable<Integer> {

    private static final Logger LOG = LoggerFactory.getLogger(GluonCommandLine.class);

    @Option(names = {"-g", "--projectGroupId"}, description = "Maven group id of generated project")
    private String projectGroupId;

    @Option(names = {"-p", "--projectArtifactId"}, description = "Maven artifact id of generated project")
    private String projectArtifactId;

    @Option(names = {"-v", "--projectVersion"}, description = "Maven version of generated project")
    private String projectVersion;

    @Option(names = {"-n", "--projectFriendlyName"}, description = "Friendly name of the project")
    private String projectFriendlyName;

    @Option(names = {"-D", "--projectDescription"}, description = "Description of the project")
    private String projectDescription;

    @Option(names = {"-b", "--basePackage"}, description = "Base package of Java sources")
    private String basePackage;

    @Option(names = {"-t", "--customTemplates"}, description = "Directory of custom templates")
    private String customTemplates;

    @Option(names = {"-P", "--projectDirectory"}, description = "Directory of generated project, must be non-existing")
    private String projectDirectory;

    @Option(names = {"-d", "--databaseVendor"}, description = "Database vendor used in generated project")
    private String databaseVendor;

    @Option(names = {"-x", "--templateExtension"}, description = "Extension of the template files. Can be used only with custom templates")
    private String templateExtension;

    @Option(names = {"-e", "--entities"}, description = "Directory where to find entity specification files")
    private String entities;

    @Option(names = {"-a", "--archetype"}, description = "Archetype (i.e. set of default templates) to use. Cannot be used with custom templates")
    private String archetype;

    @Option(names = {"-T", "--projectType"}, description = "Type of project")
    private String projectType;

    @Override
    public Integer call() throws Exception {
        try {
            final GluonBuilder gluonBuilder = new GluonBuilder();

            // all this optional hassle means: let gluon builder set the defaults, don't bother here
            Optional.ofNullable(customTemplates).map(Paths::get).ifPresent(gluonBuilder::customTemplatesDirectory);
            Optional.ofNullable(projectDirectory).map(Paths::get).ifPresent(gluonBuilder::projectDirectory);
            Optional.ofNullable(projectGroupId).ifPresent(gluonBuilder::groupId);
            Optional.ofNullable(projectArtifactId).ifPresent(gluonBuilder::artifactId);
            Optional.ofNullable(projectVersion).ifPresent(gluonBuilder::version);
            Optional.ofNullable(projectFriendlyName).ifPresent(gluonBuilder::friendlyName);
            Optional.ofNullable(projectDescription).ifPresent(gluonBuilder::description);
            Optional.ofNullable(basePackage).ifPresent(gluonBuilder::basePackage);
            Optional.ofNullable(databaseVendor).map(DatabaseVendor::valueOf).ifPresent(gluonBuilder::databaseVendor);
            Optional.ofNullable(templateExtension).ifPresent(gluonBuilder::templateExtension);
            Optional.ofNullable(entities).map(Paths::get).ifPresent(gluonBuilder::entityDirectory);
            Optional.ofNullable(archetype).ifPresent(gluonBuilder::archetype);
            Optional.ofNullable(projectType).ifPresent(gluonBuilder::projectType);

            gluonBuilder.createGluon().createProject();
            return 0;
        } catch (Exception e) {
            if (LOG.isDebugEnabled()) {
                LOG.error("Unable to generate project", e);
            } else {
                // do not print stack trace
                LOG.error("Unable to generate project: " + e.getMessage());
            }
            return 1;
        }
    }
}
