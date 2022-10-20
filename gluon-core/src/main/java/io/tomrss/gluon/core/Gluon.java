package io.tomrss.gluon.core;

import io.tomrss.gluon.core.model.EntityTemplateModel;
import io.tomrss.gluon.core.model.GlobalTemplateModel;
import io.tomrss.gluon.core.model.ModelFactory;
import io.tomrss.gluon.core.model.TemplateModel;
import io.tomrss.gluon.core.spec.EntitySpec;
import io.tomrss.gluon.core.spec.EntitySpecLoader;
import io.tomrss.gluon.core.spec.ProjectSpec;
import io.tomrss.gluon.core.template.StringTemplate;
import io.tomrss.gluon.core.template.TemplateManager;
import io.tomrss.gluon.core.template.impl.StringTemplateImpl;
import org.apache.commons.io.FileExistsException;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Predicate;

import static io.tomrss.gluon.core.template.impl.StringTemplateImpl.ENTITY_TEMPLATE_PATTERN;

public class Gluon {

    public static final List<String> RAW_FILES_RESOURCES = List.of(
            ".gitignore",
            ".dockerignore",
            "mvnw",
            "mvnw.cmd",
            ".mvn/wrapper/maven-wrapper.properties",
            ".mvn/wrapper/maven-wrapper.jar",
            ".mvn/wrapper/MavenWrapperDownloader.java"
    );
    public static final String RAW_BASE_PATH = "raw/";

    private static final Predicate<String> IS_ENTITY_TEMPLATE = path -> ENTITY_TEMPLATE_PATTERN.matcher(path).find();

    private final TemplateManager templateManager;
    private final Path projectDirectory;
    private final ProjectSpec projectSpec;
    private final StringTemplate stringTemplate;
    private final String templateExtension;
    private final EntitySpecLoader entitySpecLoader;

    Gluon(TemplateManager templateManager,
          Path projectDirectory,
          ProjectSpec projectSpec,
          String templateExtension,
          EntitySpecLoader entitySpecLoader) {
        this.templateManager = templateManager;
        this.projectDirectory = projectDirectory;
        this.projectSpec = projectSpec;
        this.templateExtension = templateExtension;
        this.entitySpecLoader = entitySpecLoader;
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

        final List<EntitySpec> entitySpecs = entitySpecLoader.load();
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
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        for (String rawFilesResource : RAW_FILES_RESOURCES) {
            final URL resource = classLoader.getResource(RAW_BASE_PATH + rawFilesResource);
            if (resource == null) {
                throw new IllegalArgumentException("Resource " + rawFilesResource + " is required");
            }
            final Path targetFile = projectDirectory.resolve(rawFilesResource);
            mkdirs(targetFile);
            try (final InputStream inputStream = resource.openStream();
                 final OutputStream outputStream = new FileOutputStream(targetFile.toFile())) {
                byte[] buffer = new byte[8 * 1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }
        }
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
        mkdirs(outPath);
        templateManager.render(templateName, model, outPath);
    }

    private Path resolveDestinationFilePath(String pathTemplate, Object model) {
        final String resolved = stringTemplate.render(pathTemplate, model);
        if (!resolved.endsWith(templateExtension)) {
            // I decided that every template must end with .gluon, just for fun.
            throw new IllegalArgumentException("Template file MUST have extension " + templateExtension);
        }
        final String resolvedWithoutGluonExtension = resolved.substring(0, resolved.lastIndexOf(templateExtension));
        return projectDirectory.resolve(resolvedWithoutGluonExtension);
    }

    private void mkdirs(Path targetFile) throws IOException {
        final Path targetFileParent = targetFile.getParent();
        if (targetFileParent != null) {
            Files.createDirectories(targetFileParent);
        }
    }
}
