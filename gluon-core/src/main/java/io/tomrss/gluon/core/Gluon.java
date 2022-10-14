package io.tomrss.gluon.core;

import io.tomrss.gluon.core.model.EntityTemplateModel;
import io.tomrss.gluon.core.model.GlobalTemplateModel;
import io.tomrss.gluon.core.model.ModelFactory;
import io.tomrss.gluon.core.model.TemplateModel;
import io.tomrss.gluon.core.persistence.DatabaseVendor;
import io.tomrss.gluon.core.spec.EntitySpec;
import io.tomrss.gluon.core.spec.EntitySpecReader;
import io.tomrss.gluon.core.spec.ProjectSpec;
import io.tomrss.gluon.core.template.StringTemplateRenderer;
import io.tomrss.gluon.core.template.TemplateRenderer;
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

    private static final Predicate<Path> IS_ENTITY_TEMPLATE = path ->
            StringTemplateRendererImpl.ENTITY_TEMPLATE_PATTERN.matcher(path.toString()).find();

    private final TemplateRenderer templateRenderer;
    private final Path templateDirectory;
    private final Path generatedProjectDirectory;
    private final Path rawFilesDirectory;
    private final StringTemplateRenderer stringTemplateRenderer;
    private final String groupId;
    private final String artifactId;
    private final String basePackage;
    private final DatabaseVendor databaseVendor;
    private final String templateExtension;
    private final EntitySpecReader entitySpecReader;

    Gluon(TemplateRenderer templateRenderer,
          Path templateDirectory,
          Path generatedProjectDirectory,
          Path rawFilesDirectory,
          String groupId,
          String artifactId,
          String basePackage,
          DatabaseVendor databaseVendor,
          String templateExtension,
          EntitySpecReader entitySpecReader) {
        this.templateRenderer = templateRenderer;
        this.templateDirectory = templateDirectory;
        this.generatedProjectDirectory = generatedProjectDirectory;
        this.rawFilesDirectory = rawFilesDirectory;
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.basePackage = basePackage;
        this.databaseVendor = databaseVendor;
        this.templateExtension = templateExtension;
        this.entitySpecReader = entitySpecReader;
        // TODO this breaks the dependency inversion principle,
        //  however in current implementation is too dangerous to depend on abstraction
        this.stringTemplateRenderer = new StringTemplateRendererImpl();

        // TODO don't really like this
        this.templateRenderer.setTemplateBaseDirectory(templateDirectory);
    }

    public void generateProject() throws IOException {
        if (Files.exists(generatedProjectDirectory)) {
            throw new FileExistsException("Directory of generated project already exists: " +
                    generatedProjectDirectory.toAbsolutePath());
        }
        Files.createDirectory(generatedProjectDirectory);

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
        // TODO apache-commons-io is imported just for this. Rewrite this method and drop apache-commons-io dependency
        FileUtils.copyDirectory(rawFilesDirectory.toFile(), generatedProjectDirectory.toFile());
    }

    private void generateGlobalSources(GlobalTemplateModel model) throws IOException {
        final List<Path> globalTemplates = listTemplateFiles(IS_ENTITY_TEMPLATE.negate());
        for (Path globalTemplate : globalTemplates) {
            renderTemplate(globalTemplate, model);
        }
    }

    private void generateEntitySources(List<EntityTemplateModel> models) throws IOException {
        final List<Path> entityTemplates = listTemplateFiles(IS_ENTITY_TEMPLATE);
        // TODO ugly but effective nested for loop, keep it?
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
        templateRenderer.render(templateName.toString(), model, outPath);
    }

    private List<Path> listTemplateFiles(Predicate<Path> predicate) throws IOException {
        try (final Stream<Path> templates = Files.walk(templateDirectory)) {
            return templates
                    .filter(Files::isRegularFile)
                    .map(templateDirectory::relativize)
                    .filter(f -> f.getFileName().toString().endsWith(templateExtension))
                    .filter(predicate)
                    .toList();
        }
    }

    private Path resolveDestinationFilePath(Path pathTemplate, Object model) {
        final String resolved = stringTemplateRenderer.render(pathTemplate.toString(), model);
        final String resolvedWithoutGluonExtension = resolved.substring(0, resolved.lastIndexOf(templateExtension));
        return Paths.get(generatedProjectDirectory.toString(), resolvedWithoutGluonExtension);
    }
}
