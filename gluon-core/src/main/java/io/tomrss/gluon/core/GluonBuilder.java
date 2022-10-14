package io.tomrss.gluon.core;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.dataformat.toml.TomlMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import io.tomrss.gluon.core.persistence.DatabaseVendor;
import io.tomrss.gluon.core.spec.EntitySpec;
import io.tomrss.gluon.core.spec.EntitySpecReader;
import io.tomrss.gluon.core.spec.impl.JacksonEntitySpecReader;
import io.tomrss.gluon.core.spec.impl.MockEntitySpecReader;
import io.tomrss.gluon.core.template.TemplateRenderer;
import io.tomrss.gluon.core.template.impl.FreemarkerTemplateRenderer;
import org.apache.commons.io.FileUtils;

import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class GluonBuilder {
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
    private DatabaseVendor databaseVendor;
    private Path generationDirectory;
    private Path rawFilesDirectory;
    private String basePackage;
    private String groupId;
    private String artifactId;
    private EntitySpecReader entitySpecReader;

    public GluonBuilder defaultTemplateRenderer(Path templateDirectory) {
        this.templateRenderer = new FreemarkerTemplateRenderer(templateDirectory);
        return this;
    }

    public GluonBuilder templateRenderer(TemplateRenderer templateRenderer) {
        this.templateRenderer = templateRenderer;
        return this;
    }

    public GluonBuilder databaseVendor(DatabaseVendor databaseVendor) {
        this.databaseVendor = databaseVendor;
        return this;
    }

    public GluonBuilder generationDirectory(Path generationDirectory) {
        this.generationDirectory = generationDirectory;
        return this;
    }

    public GluonBuilder rawFilesDirectory(Path rawFilesDirectory) {
        this.rawFilesDirectory = rawFilesDirectory;
        return this;
    }

    public GluonBuilder basePackage(String basePackage) {
        this.basePackage = basePackage;
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
                databaseVendor,
                generationDirectory,
                rawFilesDirectory,
                basePackage,
                groupId,
                artifactId,
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
        // TODO default templates from classpath
        if (databaseVendor == null) {
            databaseVendor = DatabaseVendor.POSTGRESQL;
        }
        if (generationDirectory == null) {
            // use artifactId relative path as default for generation directory
            this.generationDirectory = Paths.get(artifactId);
        }
        if (rawFilesDirectory == null) {
            // default to freemarker renderer in first existing GLUON_TEMPLATE_FOLDERS directory
            rawFilesDirectory = getFirstExistingDirectory("rawFiles", DEFAULT_RAW_FOLDERS);
        }
        if (templateRenderer == null) {
            // default to freemarker renderer in first existing GLUON_TEMPLATE_FOLDERS directory
            final Path templateDirectory = getFirstExistingDirectory("template", DEFAULT_TEMPLATE_FOLDERS);
            templateRenderer = new FreemarkerTemplateRenderer(templateDirectory);
        }
        if (basePackage == null) {
            basePackage = groupId + "." + artifactId.replaceAll("-", "");
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