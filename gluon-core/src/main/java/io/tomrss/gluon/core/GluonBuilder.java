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
import io.tomrss.gluon.core.template.TemplateRenderer;
import io.tomrss.gluon.core.template.impl.FreemarkerTemplateRenderer;
import io.tomrss.gluon.core.util.CaseUtils;
import org.apache.commons.io.FileUtils;

import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@SuppressWarnings("java:S1192") // I like to duplicate string literals for the sake of readability
public class GluonBuilder {
    // TODO sonarlint says these regex can overflow stack for large inputs, find another
    public static final Pattern ARTIFACT_ID_PATTERN = Pattern.compile("^[a-z]+(-[a-z]+)*$");
    public static final Pattern GROUP_ID_PATTERN = Pattern.compile("^[a-z]+(\\.[a-z]+)*$");
    public static final Pattern PACKAGE_PATTERN = Pattern.compile("^[a-z]+(\\.[a-z]+)*$");

    public static final List<Path> DEFAULT_ENTITY_FOLDERS = List.of(
            Paths.get(".gluon", "entity")
    );
    public static final List<Path> DEFAULT_TEMPLATE_FOLDERS = List.of(
            Paths.get(".gluon", "template"),
            Paths.get(FileUtils.getUserDirectoryPath(), ".gluon", "template")
    );
    public static final List<Path> DEFAULT_RAW_FOLDERS = List.of(
            Paths.get(".gluon", "raw"),
            Paths.get(FileUtils.getUserDirectoryPath(), ".gluon", "raw")
    );

    private TemplateRenderer templateRenderer;
    private Path templateDirectory;
    private Path projectDirectory;
    private Path rawFilesDirectory;
    private String basePackage;
    private String groupId;
    private String artifactId;
    private String version;
    private String friendlyName;
    private String description;
    private DatabaseVendor databaseVendor;
    private String templateExtension;
    private EntitySpecReader entitySpecReader;

    public GluonBuilder templateRenderer(TemplateRenderer templateRenderer) {
        this.templateRenderer = templateRenderer;
        return this;
    }

    public GluonBuilder templateDirectory(Path templateDirectory) {
        this.templateDirectory = templateDirectory;
        return this;
    }

    public GluonBuilder projectDirectory(Path projectDirectory) {
        this.projectDirectory = projectDirectory;
        return this;
    }

    public GluonBuilder rawFilesDirectory(Path rawFilesDirectory) {
        this.rawFilesDirectory = rawFilesDirectory;
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

    public Gluon createGluon() throws FileNotFoundException {
        validateBuild();
        setDefaults();
        return new Gluon(templateRenderer,
                templateDirectory,
                projectDirectory,
                rawFilesDirectory,
                new ProjectSpec(groupId, artifactId, version, friendlyName, description, basePackage, databaseVendor),
                templateExtension,
                entitySpecReader);
    }

    private void validateBuild() {
        final List<String> failedValidations = new ArrayList<>();

        if (artifactId == null) {
            failedValidations.add("artifactId is required");
        } else if (!ARTIFACT_ID_PATTERN.matcher(artifactId).matches()) {
            failedValidations.add("artifactId should match regex" + ARTIFACT_ID_PATTERN);
        }
        if (groupId == null) {
            failedValidations.add("groupId is required");
        } else if (!GROUP_ID_PATTERN.matcher(groupId).matches()) {
            failedValidations.add("groupId should match regex" + GROUP_ID_PATTERN);
        }
        if (basePackage != null && !PACKAGE_PATTERN.matcher(basePackage).matches()) {
            failedValidations.add("basePackage should match regex" + PACKAGE_PATTERN);
        }

        if (!failedValidations.isEmpty()) {
            throw new IllegalArgumentException("Unable to build Gluon, following validations failed: " +
                    "[" + String.join("], [", failedValidations) + "]");
        }
    }

    private void setDefaults() throws FileNotFoundException {
        if (templateRenderer == null) {
            templateRenderer = new FreemarkerTemplateRenderer();
        }
        if (templateDirectory == null) {
            // TODO if this does not exist, default templates should be in jar and loaded from resources?
            templateDirectory = getFirstExistingDirectory("template", DEFAULT_TEMPLATE_FOLDERS);
        }
        if (projectDirectory == null) {
            // use artifactId relative path as default for generation directory
            projectDirectory = Paths.get(artifactId);
        }
        if (rawFilesDirectory == null) {
            rawFilesDirectory = getFirstExistingDirectory("rawFiles", DEFAULT_RAW_FOLDERS);
        }
        if (version == null) {
            version = "0.1.0";
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
            templateExtension = ".gluon";
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
}