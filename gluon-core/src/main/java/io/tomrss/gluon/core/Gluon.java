package io.tomrss.gluon.core;

import io.tomrss.gluon.core.model.EntityTemplateModel;
import io.tomrss.gluon.core.model.GlobalTemplateModel;
import io.tomrss.gluon.core.model.ModelFactory;
import io.tomrss.gluon.core.model.TemplateModel;
import io.tomrss.gluon.core.spec.EntitySpec;
import io.tomrss.gluon.core.spec.EntitySpecLoader;
import io.tomrss.gluon.core.spec.ProjectSpec;
import io.tomrss.gluon.core.spec.ProjectType;
import io.tomrss.gluon.core.template.StringTemplate;
import io.tomrss.gluon.core.template.TemplateManager;
import io.tomrss.gluon.core.template.impl.StringTemplateImpl;
import io.tomrss.gluon.core.util.ResourceUtils;
import org.apache.commons.io.FileExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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
    public static final String RAW_METADATA_FILENAME = "raw-metadata.txt";

    private static final Logger LOG = LoggerFactory.getLogger(Gluon.class);

    private static final Predicate<String> IS_ENTITY_TEMPLATE = path -> ENTITY_TEMPLATE_PATTERN.matcher(path).find();

    private final TemplateManager templateManager;
    private final ProjectType projectType;
    private final Path projectDirectory;
    private final ProjectSpec projectSpec;
    private final StringTemplate stringTemplate;
    private final String templateExtension;
    private final EntitySpecLoader entitySpecLoader;

    Gluon(TemplateManager templateManager,
          ProjectType projectType,
          Path projectDirectory,
          ProjectSpec projectSpec,
          String templateExtension,
          EntitySpecLoader entitySpecLoader) {
        this.templateManager = templateManager;
        this.projectType = projectType;
        this.projectDirectory = projectDirectory;
        this.projectSpec = projectSpec;
        this.templateExtension = templateExtension;
        this.entitySpecLoader = entitySpecLoader;
        // this breaks the dependency inversion principle, but in current impl is too dangerous to depend on abstraction
        this.stringTemplate = new StringTemplateImpl();
    }

    public void generateProject() throws IOException {
        LOG.info("Generating project {} in directory {} ...", projectSpec.artifactId(), projectDirectory.toAbsolutePath());
        checkProjectDirectory();
        final List<EntitySpec> entitySpecs = loadEntities();
        final TemplateModel templateModel = initTemplateModel(entitySpecs);
        extractRawFiles();
        generateSources(templateModel);
        LOG.info("Project {} generated.", projectSpec.artifactId());
    }

    private TemplateModel initTemplateModel(List<EntitySpec> entitySpecs) {
        final ModelFactory modelFactory = new ModelFactory(projectSpec);
        final TemplateModel templateModel = modelFactory.buildModelForEntities(entitySpecs);
        LOG.info("Template model initialized");
        return templateModel;
    }

    private List<EntitySpec> loadEntities() throws IOException {
        final List<EntitySpec> entitySpecs = entitySpecLoader.load();
        if (LOG.isInfoEnabled()) {
            LOG.info("Loaded {} entities: {}", entitySpecs.size(), entitySpecs.stream()
                    .map(EntitySpec::name)
                    .collect(Collectors.joining(", ")));
        }
        return entitySpecs;
    }

    private void checkProjectDirectory() throws IOException {
        if (Files.exists(projectDirectory)) {
            throw new FileExistsException("Directory of project already exists: " + projectDirectory.toAbsolutePath());
        }
        Files.createDirectory(projectDirectory);
    }

    private void extractRawFiles() throws IOException {
        LOG.info("Extracting raw files...");
        final String rawPackage = RAW_BASE_PATH + projectType.name().toLowerCase() + "/";
        final List<String> rawFilesResources = ResourceUtils.readMetadataFile(rawPackage + RAW_METADATA_FILENAME);
        for (String rawFileResource : rawFilesResources) {
            final Path target = projectDirectory.resolve(rawFileResource);
            LOG.debug("Extracting raw file {} to {}", rawFileResource, target);
            mkdirs(target);
            ResourceUtils.extractResource(rawPackage + rawFileResource, target);
        }
        LOG.info("Extracted {} raw files.", RAW_FILES_RESOURCES.size());
    }

    private void generateSources(TemplateModel templateModel) throws IOException {
        final List<String> templates = templateManager.listTemplates(templateExtension);
        generateGlobalSources(templateModel.getGlobalModel(), templates);
        generateEntitySources(templateModel.getEntityModels(), templates);
    }

    private void generateGlobalSources(GlobalTemplateModel model, List<String> templateFiles) throws IOException {
        LOG.info("Generating global sources...");
        final List<String> globalTemplates = templateFiles.stream()
                .filter(IS_ENTITY_TEMPLATE.negate())
                .toList();
        for (String globalTemplate : globalTemplates) {
            renderTemplate(globalTemplate, model);
        }
        LOG.info("Generated {} global sources.", globalTemplates.size());
    }

    private void generateEntitySources(List<EntityTemplateModel> models, List<String> templateFiles) throws IOException {
        LOG.info("Generating entity sources...");
        final List<String> entityTemplates = templateFiles.stream()
                .filter(IS_ENTITY_TEMPLATE)
                .toList();
        LOG.debug("Found {} entity templates", entityTemplates.size());
        int count = 0;
        for (EntityTemplateModel model : models) {
            LOG.debug("Generating sources for entity {}", model.getEntity().getName());
            for (String entityTemplate : entityTemplates) {
                renderTemplate(entityTemplate, model);
                count++;
            }
        }
        LOG.info("Generated {} entity sources.", count);
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

    private static void mkdirs(Path targetFile) throws IOException {
        final Path targetFileParent = targetFile.getParent();
        if (targetFileParent != null) {
            Files.createDirectories(targetFileParent);
        }
    }
}
