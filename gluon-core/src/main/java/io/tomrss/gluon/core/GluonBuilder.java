package io.tomrss.gluon.core;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.dataformat.toml.TomlMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import io.tomrss.gluon.core.persistence.DatabaseVendor;
import io.tomrss.gluon.core.spec.EntitySpec;
import io.tomrss.gluon.core.spec.EntitySpecReader;
import io.tomrss.gluon.core.spec.ProjectSpec;
import io.tomrss.gluon.core.spec.impl.JacksonEntitySpecReader;
import io.tomrss.gluon.core.spec.impl.MockEntitySpecReader;
import io.tomrss.gluon.core.template.TemplateManager;
import io.tomrss.gluon.core.template.impl.FileFreemarkerTemplateManager;
import io.tomrss.gluon.core.template.impl.GluonArchetypeTemplateManager;
import io.tomrss.gluon.core.util.CaseUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings("java:S1192") // I like to duplicate string literals for the sake of readability
public class GluonBuilder {

    public static final Pattern ARTIFACT_ID_PATTERN = Pattern.compile("^[a-z]+(-[a-z]+)*$");
    public static final Pattern PACKAGE_PATTERN = Pattern.compile("^[a-z]+(\\.[a-z]+)*$");

    public static final String DEFAULT_ARCHETYPE = "quarkus-gluon-default";
    public static final String DEFAULT_GROUP_ID = "org.acme";
    public static final String DEFAULT_ARTIFACT_ID = "gluon-example";
    public static final String DEFAULT_VERSION = "0.1.0";
    public static final String DEFAULT_TEMPLATE_EXTENSION = ".gluon";
    public static final List<Path> DEFAULT_ENTITY_FOLDERS = List.of(Paths.get(".gluon", "entity"));

    private TemplateManager templateManager;
    private Path customTemplatesDirectory;
    private String archetype;
    private Path projectDirectory;
    private String groupId;
    private String artifactId;
    private String version;
    private String friendlyName;
    private String description;
    private String basePackage;
    private String imageRegistry;
    private DatabaseVendor databaseVendor;
    private String templateExtension;
    private EntitySpecReader entitySpecReader;

    public GluonBuilder templateManager(TemplateManager templateManager) {
        this.templateManager = templateManager;
        return this;
    }

    public GluonBuilder customTemplates(Path customTemplatesDirectory) {
        this.customTemplatesDirectory = customTemplatesDirectory;
        return this;
    }

    public GluonBuilder archetype(String archetype) {
        this.archetype = archetype;
        return this;
    }

    public GluonBuilder projectDirectory(Path projectDirectory) {
        this.projectDirectory = projectDirectory;
        return this;
    }

    public GluonBuilder groupId(String groupId) {
        this.groupId = groupId;
        return this;
    }

    public GluonBuilder artifactId(String artifactId) {
        this.artifactId = artifactId;
        return this;
    }

    public GluonBuilder version(String version) {
        this.version = version;
        return this;
    }

    public GluonBuilder friendlyName(String friendlyName) {
        this.friendlyName = friendlyName;
        return this;
    }

    public GluonBuilder description(String description) {
        this.description = description;
        return this;
    }

    public GluonBuilder basePackage(String basePackage) {
        this.basePackage = basePackage;
        return this;
    }

    public GluonBuilder imageRegistry(String imageRegistry) {
        this.imageRegistry = imageRegistry;
        return this;
    }

    public GluonBuilder databaseVendor(DatabaseVendor databaseVendor) {
        this.databaseVendor = databaseVendor;
        return this;
    }

    public GluonBuilder templateExtension(String templateExtension) {
        this.templateExtension = templateExtension;
        return this;
    }

    public GluonBuilder mockEntities(List<EntitySpec> mockEntities) {
        if (entitySpecReader != null) {
            throw new IllegalArgumentException("Cannot set mock entities when Gluon is already configured to read from file");
        }
        this.entitySpecReader = new MockEntitySpecReader(mockEntities);
        return this;
    }

    public GluonBuilder readEntitiesFromJson(Path jsonEntitiesPath) {
        if (entitySpecReader != null) {
            throw new IllegalArgumentException("Cannot read from multiple sources");
        }
        this.entitySpecReader = new JacksonEntitySpecReader(jsonEntitiesPath, new JsonMapper());
        return this;
    }

    public GluonBuilder readEntitiesFromYaml(Path yamlEntitiesPath) {
        if (entitySpecReader != null) {
            throw new IllegalArgumentException("Cannot read from multiple sources");
        }
        this.entitySpecReader = new JacksonEntitySpecReader(yamlEntitiesPath, new YAMLMapper());
        return this;
    }

    public GluonBuilder readEntitiesFromXml(Path xmlEntitiesPath) {
        if (entitySpecReader != null) {
            throw new IllegalArgumentException("Cannot read from multiple sources");
        }
        this.entitySpecReader = new JacksonEntitySpecReader(xmlEntitiesPath, new XmlMapper());
        return this;
    }

    public GluonBuilder readEntitiesFromToml(Path tomlEntitiesPath) {
        if (entitySpecReader != null) {
            throw new IllegalArgumentException("Cannot read from multiple sources");
        }
        this.entitySpecReader = new JacksonEntitySpecReader(tomlEntitiesPath, new TomlMapper());
        return this;
    }

    public Gluon createGluon() {
        try {
            validateBuild();
            setDefaults();
            return new Gluon(templateManager,
                    projectDirectory,
                    new ProjectSpec(groupId,
                            artifactId,
                            version,
                            friendlyName,
                            description,
                            basePackage,
                            imageRegistry,
                            databaseVendor),
                    templateExtension,
                    entitySpecReader);
        } catch (Exception e) {
            throw new GluonInitException("Unable to create Gluon instance: " + e.getMessage(), e);
        }
    }

    private void validateBuild() {
        final List<String> failedValidations = new ArrayList<>();

        final long countOfNonNullTemplateProperties = Stream.of(templateManager, archetype, customTemplatesDirectory)
                .filter(Objects::nonNull)
                .count();
        if (countOfNonNullTemplateProperties > 1) {
            failedValidations.add("Properties 'templateManager', 'archetype', 'customTemplates' are mutually exclusive");
        }
        if (artifactId != null && !ARTIFACT_ID_PATTERN.matcher(artifactId).matches()) {
            failedValidations.add("Project artifact id should match regex" + ARTIFACT_ID_PATTERN);
        }
        if (groupId != null && !PACKAGE_PATTERN.matcher(groupId).matches()) {
            failedValidations.add("Project group id should match regex" + PACKAGE_PATTERN);
        }
        if (basePackage != null && !PACKAGE_PATTERN.matcher(basePackage).matches()) {
            failedValidations.add("Base package should match regex" + PACKAGE_PATTERN);
        }

        if (!failedValidations.isEmpty()) {
            throw new IllegalArgumentException("Validations failed: " +
                    "[" + String.join("], [", failedValidations) + "]");
        }
    }

    private void setDefaults() throws IOException {
        if (templateManager == null) {
            if (customTemplatesDirectory != null) {
                templateManager = new FileFreemarkerTemplateManager(customTemplatesDirectory);
            } else {
                final String archetypeName = Objects.requireNonNullElse(archetype, DEFAULT_ARCHETYPE);
                final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
                templateManager = new GluonArchetypeTemplateManager(classLoader, archetypeName);
            }
        }
        if (projectDirectory == null) {
            // use artifactId relative path as default for generation directory
            projectDirectory = Paths.get(artifactId);
        }
        if (groupId == null) {
            groupId = DEFAULT_GROUP_ID;
        }
        if (artifactId == null) {
            artifactId = DEFAULT_ARTIFACT_ID;
        }
        if (version == null) {
            version = DEFAULT_VERSION;
        }
        if (friendlyName == null) {
            friendlyName = CaseUtils.hyphenSeparatedToDescriptive(artifactId);
        }
        if (basePackage == null) {
            basePackage = groupId + "." + artifactId.replace("-", "");
        }
        if (databaseVendor == null) {
            databaseVendor = DatabaseVendor.POSTGRESQL;
        }
        if (templateExtension == null) {
            templateExtension = DEFAULT_TEMPLATE_EXTENSION;
        }
        if (entitySpecReader == null) {
            final Path entityDirectory = getFirstExistingDirectory("entity", DEFAULT_ENTITY_FOLDERS);
            entitySpecReader = new JacksonEntitySpecReader(entityDirectory, new JsonMapper());
        }
    }

    private static Path getFirstExistingDirectory(String directoryUsage, List<Path> paths) throws FileNotFoundException {
        return paths.stream()
                .filter(Files::exists)
                .filter(Files::isDirectory)
                .findFirst()
                .orElseThrow(() -> new FileNotFoundException("Directory " + directoryUsage + " not found in " +
                        paths.stream()
                                .map(Path::toAbsolutePath)
                                .map(Path::toString)
                                .collect(Collectors.joining(", "))));
    }

    public static class GluonInitException extends RuntimeException {
        public GluonInitException() {
        }

        public GluonInitException(String message) {
            super(message);
        }

        public GluonInitException(String message, Throwable cause) {
            super(message, cause);
        }

        public GluonInitException(Throwable cause) {
            super(cause);
        }

        public GluonInitException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
            super(message, cause, enableSuppression, writableStackTrace);
        }
    }
}