package io.tomrss.gluon.core;

import io.tomrss.gluon.core.model.EntityTemplateModel;
import io.tomrss.gluon.core.model.GlobalTemplateModel;
import io.tomrss.gluon.core.model.ModelFactory;
import io.tomrss.gluon.core.model.TemplateModel;
import io.tomrss.gluon.core.persistence.DatabaseVendor;
import io.tomrss.gluon.core.spec.EntitySpec;
import io.tomrss.gluon.core.spec.EntitySpecReader;
import io.tomrss.gluon.core.spec.ProjectSpec;
import io.tomrss.gluon.core.template.FileTemplateRenderer;
import io.tomrss.gluon.core.template.StringTemplateRenderer;
import io.tomrss.gluon.core.template.impl.StringTemplateRendererImpl;
import org.apache.commons.io.FileExistsException;
import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class Gluon {
    public static final String TEMPLATE_EXTENSION = ".gluon";

    private static final Predicate<Path> IS_ENTITY_TEMPLATE = path ->
            StringTemplateRendererImpl.ENTITY_TEMPLATE_PATTERN.matcher(path.toString()).find();

    private final FileTemplateRenderer fileTemplateRenderer;
    private final StringTemplateRenderer stringTemplateRenderer;
    private final DatabaseVendor databaseVendor;
    private final Path generatedProjectPath;
    private final Path rawFilesDirectory;
    private final String basePackage;
    private final EntitySpecReader entitySpecReader;
    private final String groupId;
    private final String artifactId;
    private final Path templatePath = Paths.get(".gluon", "template"); // TODO

    Gluon(FileTemplateRenderer fileTemplateRenderer,
          DatabaseVendor databaseVendor,
          Path generatedProjectPath,
          Path rawFilesDirectory,
          String basePackage,
          String groupId,
          String artifactId,
          EntitySpecReader entitySpecReader) {
        // TODO just have a project spec here built from builder
        this.fileTemplateRenderer = fileTemplateRenderer;
        this.databaseVendor = databaseVendor;
        this.generatedProjectPath = generatedProjectPath;
        this.rawFilesDirectory = rawFilesDirectory;
        this.basePackage = basePackage;
        this.entitySpecReader = entitySpecReader;
        this.groupId = groupId;
        this.artifactId = artifactId;
        // TODO this breaks the dependency inversion principle,
        //  however in current implementation is too dangerous to depend on abstraction
        this.stringTemplateRenderer = new StringTemplateRendererImpl();
    }

    public void generateProject() throws IOException {
        if (Files.exists(generatedProjectPath)) {
            throw new FileExistsException("Directory of generated project already exists: " +
                    generatedProjectPath.toAbsolutePath());
        }
        Files.createDirectory(generatedProjectPath);

        final List<EntitySpec> entitySpecs = entitySpecReader.read();

        final ProjectSpec projectSpec = new ProjectSpec(groupId, artifactId, basePackage, databaseVendor);
        final ModelFactory modelFactory = new ModelFactory(projectSpec);
        final TemplateModel templateModel = modelFactory.buildModelForEntities(entitySpecs);

        copyRawFiles();
        generateSources(templateModel);
    }

    private void generateSources(TemplateModel templateModel) throws IOException {
        generateGlobalSources(templateModel.getGlobalModel());
        generateEntitySources(templateModel.getEntityModels());
    }

    private void copyRawFiles() throws IOException {
        FileUtils.copyDirectory(rawFilesDirectory.toFile(), generatedProjectPath.toFile());
    }

    private void generateGlobalSources(GlobalTemplateModel model) throws IOException {
        final List<Path> globalTemplates = listTemplateFiles(IS_ENTITY_TEMPLATE.negate());
        for (Path globalTemplate : globalTemplates) {
            renderTemplate(globalTemplate, model);
        }
    }

    private void generateEntitySources(List<EntityTemplateModel> models) throws IOException {
        final List<Path> entityTemplates = listTemplateFiles(IS_ENTITY_TEMPLATE);
        for (Path entityTemplate : entityTemplates) {
            for (EntityTemplateModel model : models) {
                renderTemplate(entityTemplate, model);
            }
        }
    }

    private void renderTemplate(Path templateName, Object model) throws IOException {
        final Path outPath = resolveDestinationFilePath(templateName, model);
        final Path parent = Files.createDirectories(outPath.getParent());
        if (parent != null) {
            Files.createDirectories(parent);
        }
        fileTemplateRenderer.render(templateName.toString(), model, outPath);
    }

    private List<Path> listTemplateFiles(Predicate<Path> predicate) throws IOException {
        try (final Stream<Path> templates = Files.walk(templatePath)) {
            return templates
                    .filter(Files::isRegularFile)
                    .map(templatePath::relativize)
                    .filter(f -> f.getFileName().toString().endsWith(TEMPLATE_EXTENSION))
                    .filter(predicate)
                    .toList();
        }
    }

    private Path resolveDestinationFilePath(Path pathTemplate, Object model) {
        final String resolved = stringTemplateRenderer.render(pathTemplate.toString(), model);
        final String resolvedWithoutGluonExtension = resolved.substring(0, resolved.lastIndexOf(TEMPLATE_EXTENSION));
        return Paths.get(generatedProjectPath.toString(), resolvedWithoutGluonExtension);
    }
}
