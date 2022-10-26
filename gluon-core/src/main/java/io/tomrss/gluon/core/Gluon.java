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
import static io.tomrss.gluon.core.util.Constants.RAW_BASE_PATH;
import static io.tomrss.gluon.core.util.Constants.RAW_METADATA_FILENAME;

/**
 * Scaffolding tool for domain-driven applications based on templates.
 * <p>
 * This is the main class holding all project bootstrapping and code generation logic.
 * For getting a Gluon instance, see {@link GluonBuilder}.
 * <p>
 * Project creation is based on resolving templates. As for any templates, Gluon templates
 * are rendered by injecting a data model into a template file. The Gluon data model contains
 * information about project specifications and configured entities.
 * <p>
 * The path at which templates are stored will be preserved in generated project.
 * The path can also contain a templating syntax, delimited by {@code {{ }}} markers.
 * Every template file referencing the variable {@code entity} in his template path
 * (for example {@code /src/main/java/mybasepackage/service/{{entity.name}}Service.java})
 * will be treated as an <b>entity scoped</b> template, whereas every other templates
 * will be treated as <b>global scoped</b> template.
 *
 * <ol>
 *     <li><b>entity scoped templates</b>: they are rendered for every configured entity.
 *     They are rendered with {@link EntityTemplateModel} data model,
 *     having access to project specification and single entity. For example, if entities are
 *     {@code Foo} and {@code Bar}, then the template {@code {{entity.name}}Service.java} will
 *     be rendered in {@code FooService.java} and {@code BarService.java}, each having access
 *     to {@code Foo} and {@code Bar} entities respectively.
 *
 *     <li><b>global scoped templates</b>: they are rendered only once.
 *     They are rendered with {@link GlobalTemplateModel} data model,
 *     having access to project specification and all entities.
 * </ol>
 *
 * @author Tommaso Rossi
 * @see GluonBuilder
 * @see TemplateManager
 * @see EntityTemplateModel
 * @see GlobalTemplateModel
 */
public class Gluon {

    private static final Logger LOG = LoggerFactory.getLogger(Gluon.class);
    private static final Predicate<String> IS_ENTITY_TEMPLATE = path -> ENTITY_TEMPLATE_PATTERN.matcher(path).find();

    private final TemplateManager templateManager;
    private final ProjectType projectType;
    private final Path projectDirectory;
    private final ProjectSpec projectSpec;
    private final StringTemplate stringTemplate;
    private final String templateExtension;
    private final EntitySpecLoader entitySpecLoader;

    /**
     * Get a Gluon instance.
     *
     * @param templateManager   template manager used for retrieving and rendering templates
     * @param projectType       type of project to manage
     * @param projectDirectory  directory of the project
     * @param projectSpec       project specification
     * @param templateExtension file extension of template files
     * @param entitySpecLoader  loader of entities for domain-driven logic
     */
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

    /**
     * Create the project configured in this Gluon instance.
     *
     * @throws IOException I/O error in extracting raw files, retrieving templates or writing files
     */
    public void createProject() throws IOException {
        LOG.info("Creating project {} in directory {} ...", projectSpec.artifactId(), projectDirectory.toAbsolutePath());
        checkProjectDirectory();
        final List<EntitySpec> entitySpecs = loadEntities();
        final TemplateModel templateModel = initTemplateModel(entitySpecs);
        extractRawFiles();
        generateSources(templateModel);
        LOG.info("Project {} created.", projectSpec.artifactId());
    }

    /**
     * Check project directory, fail if exists and create it if not.
     *
     * @throws FileExistsException project directory already exists
     * @throws IOException         error creating directory
     */
    private void checkProjectDirectory() throws IOException {
        if (Files.exists(projectDirectory)) {
            throw new FileExistsException("Directory of project already exists: " + projectDirectory.toAbsolutePath());
        }
        Files.createDirectory(projectDirectory);
    }

    /**
     * Load entities to be managed in target project.
     *
     * @return entity specifications
     * @throws IOException error loading entities
     */
    private List<EntitySpec> loadEntities() throws IOException {
        final List<EntitySpec> entitySpecs = entitySpecLoader.load();
        if (LOG.isInfoEnabled()) {
            LOG.info("Loaded {} entities: {}", entitySpecs.size(), entitySpecs.stream()
                    .map(EntitySpec::name)
                    .collect(Collectors.joining(", ")));
        }
        return entitySpecs;
    }

    /**
     * Initialize the template data model based on entity specifications.
     *
     * @param entitySpecs entity specifications
     * @return template model
     */
    private TemplateModel initTemplateModel(List<EntitySpec> entitySpecs) {
        final ModelFactory modelFactory = new ModelFactory(projectSpec);
        final TemplateModel templateModel = modelFactory.buildModelForEntities(entitySpecs);
        LOG.info("Template model initialized");
        return templateModel;
    }

    /**
     * Extract raw files for current project type into project directory.
     * <p>
     * Raw files are stored in jar resources for every project type and loaded
     * by looking at a metadata file.
     *
     * @throws IOException error reading resources or writing to file
     */
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
        LOG.info("Extracted {} raw files.", rawFilesResources.size());
    }

    /**
     * Generate sources by rendering templates with template model.
     *
     * @param templateModel template model for resolving templates
     * @throws IOException error rendering templates
     */
    private void generateSources(TemplateModel templateModel) throws IOException {
        final List<String> templates = templateManager.listTemplates(templateExtension);
        final List<String> globalTemplates = templates.stream()
                .filter(IS_ENTITY_TEMPLATE.negate())
                .toList();
        final List<String> entityTemplates = templates.stream()
                .filter(IS_ENTITY_TEMPLATE)
                .toList();
        generateGlobalSources(templateModel.getGlobalModel(), globalTemplates);
        generateEntitySources(templateModel.getEntityModels(), entityTemplates);
    }

    /**
     * Generate global sources.
     *
     * @param model           global template model
     * @param globalTemplates global template names
     * @throws IOException error rendering templates
     */
    private void generateGlobalSources(GlobalTemplateModel model, List<String> globalTemplates) throws IOException {
        LOG.info("Generating global sources...");
        for (String globalTemplate : globalTemplates) {
            renderTemplate(globalTemplate, model);
        }
        LOG.info("Generated {} global sources.", globalTemplates.size());
    }

    /**
     * Generate entity sources.
     *
     * @param models          entity template models
     * @param entityTemplates entity template names
     * @throws IOException error rendering templates
     */
    private void generateEntitySources(List<EntityTemplateModel> models, List<String> entityTemplates) throws IOException {
        LOG.info("Generating entity sources...");
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

    /**
     * Render template.
     *
     * @param templateName name of the template
     * @param model        model of the template
     * @throws IOException error rendering template
     */
    private void renderTemplate(String templateName, Object model) throws IOException {
        final Path outPath = resolveDestinationFilePath(templateName, model);
        mkdirs(outPath);
        templateManager.render(templateName, model, outPath);
    }

    /**
     * Resolve the destination of template as file path.
     *
     * @param pathTemplate path template, can contain {@code {{ }}} markers.
     * @param model        model of path template
     * @return resolved path
     */
    private Path resolveDestinationFilePath(String pathTemplate, Object model) {
        final String resolved = stringTemplate.render(pathTemplate, model);
        if (!resolved.endsWith(templateExtension)) {
            // I decided that every template must end with .gluon, just for fun.
            throw new IllegalArgumentException("Template file MUST have extension " + templateExtension);
        }
        final String resolvedWithoutGluonExtension = resolved.substring(0, resolved.lastIndexOf(templateExtension));
        return projectDirectory.resolve(resolvedWithoutGluonExtension);
    }

    /**
     * Make parent directory of file
     *
     * @param file file
     * @throws IOException unable to create parent directories
     */
    private static void mkdirs(Path file) throws IOException {
        final Path targetFileParent = file.getParent();
        if (targetFileParent != null) {
            Files.createDirectories(targetFileParent);
        }
    }
}
