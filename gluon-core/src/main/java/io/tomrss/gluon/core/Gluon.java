package io.tomrss.gluon.core;

import io.tomrss.gluon.core.model.EntityTemplateModel;
import io.tomrss.gluon.core.model.GlobalTemplateModel;
import io.tomrss.gluon.core.model.ModelFactory;
import io.tomrss.gluon.core.model.TemplateModel;
import io.tomrss.gluon.core.spec.EntitySpec;
import io.tomrss.gluon.core.spec.EntitySpecReader;
import io.tomrss.gluon.core.spec.ProjectSpec;
import io.tomrss.gluon.core.template.StringTemplate;
import io.tomrss.gluon.core.template.TemplateManager;
import io.tomrss.gluon.core.template.impl.StringTemplateImpl;
import org.apache.commons.io.FileExistsException;
import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Predicate;

public class Gluon {

    private static final Predicate<String> IS_ENTITY_TEMPLATE = path ->
            StringTemplateImpl.ENTITY_TEMPLATE_PATTERN.matcher(path).find();

    private final TemplateManager templateManager;
    private final Path projectDirectory;
    private final Path rawFilesDirectory;
    private final ProjectSpec projectSpec;
    private final StringTemplate stringTemplate;
    private final String templateExtension;
    private final EntitySpecReader entitySpecReader;

    Gluon(TemplateManager templateManager,
          Path projectDirectory,
          Path rawFilesDirectory,
          ProjectSpec projectSpec,
          String templateExtension,
          EntitySpecReader entitySpecReader) {
        this.templateManager = templateManager;
        this.projectDirectory = projectDirectory;
        this.rawFilesDirectory = rawFilesDirectory;
        this.projectSpec = projectSpec;
        this.templateExtension = templateExtension;
        this.entitySpecReader = entitySpecReader;
        // TODO this breaks the dependency inversion principle,
        //  however in current implementation is too dangerous to depend on abstraction
        this.stringTemplate = new StringTemplateImpl();
    }

    public void generateProject() throws IOException {
        if (Files.exists(projectDirectory)) {
            throw new FileExistsException("Directory of generated project already exists: " +
                    projectDirectory.toAbsolutePath());
        }
        Files.createDirectory(projectDirectory);

        final List<EntitySpec> entitySpecs = entitySpecReader.read();

        final ModelFactory modelFactory = new ModelFactory(projectSpec);
        final TemplateModel templateModel = modelFactory.buildModelForEntities(entitySpecs);

        copyRawFiles();
        generateSources(templateModel);
    }

    private void generateSources(TemplateModel templateModel) throws IOException {
        final List<String> templates = templateManager.listTemplates(templateExtension);
        generateGlobalSources(templateModel.getGlobalModel(), templates);
        generateEntitySources(templateModel.getEntityModels(), templates);
    }

    private void copyRawFiles() throws IOException {
        // TODO apache-commons-io is imported just for this. Rewrite this method and drop apache-commons-io dependency
        FileUtils.copyDirectory(rawFilesDirectory.toFile(), projectDirectory.toFile());
    }

    private void generateGlobalSources(GlobalTemplateModel model, List<String> templateFiles) throws IOException {
        final List<String> globalTemplates = templateFiles.stream()
                .filter(IS_ENTITY_TEMPLATE.negate())
                .toList();
        for (String globalTemplate : globalTemplates) {
            renderTemplate(globalTemplate, model);
        }
    }

    private void generateEntitySources(List<EntityTemplateModel> models, List<String> templateFiles) throws IOException {
        final List<String> entityTemplates = templateFiles.stream()
                .filter(IS_ENTITY_TEMPLATE)
                .toList();
        for (String entityTemplate : entityTemplates) {
            for (EntityTemplateModel model : models) {
                renderTemplate(entityTemplate, model);
            }
        }
    }

    private void renderTemplate(String templateName, Object model) throws IOException {
        final Path outPath = resolveDestinationFilePath(templateName, model);
        final Path parent = Files.createDirectories(outPath.getParent());
        if (parent != null) {
            Files.createDirectories(parent);
        }
        templateManager.render(templateName, model, outPath);
    }

    private Path resolveDestinationFilePath(String pathTemplate, Object model) {
        final String resolved = stringTemplate.render(pathTemplate, model);
        if (!resolved.endsWith(templateExtension)) {
            // I decided that every template must end with .gluon, just for fun.
            throw new IllegalArgumentException("Template file MUST have extension " + templateExtension);
        }
        final String resolvedWithoutGluonExtension = resolved.substring(0, resolved.lastIndexOf(templateExtension));
        return Paths.get(projectDirectory.toString(), resolvedWithoutGluonExtension);
    }
}
