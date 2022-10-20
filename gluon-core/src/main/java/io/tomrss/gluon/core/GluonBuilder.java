package io.tomrss.gluon.core;

import io.tomrss.gluon.core.persistence.DatabaseVendor;
import io.tomrss.gluon.core.spec.EntitySpec;
import io.tomrss.gluon.core.spec.EntitySpecLoader;
import io.tomrss.gluon.core.spec.ProjectSpec;
import io.tomrss.gluon.core.spec.SpecFormat;
import io.tomrss.gluon.core.spec.impl.JacksonEntitySpecLoader;
import io.tomrss.gluon.core.spec.impl.MockEntitySpecLoader;
import io.tomrss.gluon.core.template.TemplateManager;
import io.tomrss.gluon.core.template.impl.FileFreemarkerTemplateManager;
import io.tomrss.gluon.core.template.impl.GluonArchetypeTemplateManager;
import io.tomrss.gluon.core.util.CaseUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@SuppressWarnings("java:S1192") // I like to duplicate string literals for the sake of readability
public class GluonBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(GluonBuilder.class);

    public static final Pattern ARTIFACT_ID_PATTERN = Pattern.compile("^[a-z]+(-[a-z]+)*$");
    public static final Pattern PACKAGE_PATTERN = Pattern.compile("^[a-z]+(\\.[a-z]+)*$");

    public static final String DEFAULT_ARCHETYPE = "quarkus-gluon-default";
    public static final String DEFAULT_GROUP_ID = "org.acme";
    public static final String DEFAULT_ARTIFACT_ID = "gluon-example";
    public static final String DEFAULT_VERSION = "0.1.0";
    public static final String DEFAULT_TEMPLATE_EXTENSION = ".gluon";
    public static final SpecFormat DEFAULT_FORMAT = SpecFormat.JSON;

    private TemplateManager templateManager;
    private Path customTemplatesDirectory;
    private Path projectDirectory;
    private Path entityDirectory;
    private SpecFormat entityFormat;
    private String archetype;
    private String groupId;
    private String artifactId;
    private String version;
    private String friendlyName;
    private String description;
    private String basePackage;
    private String imageRegistry;
    private DatabaseVendor databaseVendor;
    private String templateExtension;
    private List<EntitySpec> mockEntities;

    public GluonBuilder templateManager(TemplateManager templateManager) {
        this.templateManager = templateManager;
        return this;
    }

    public GluonBuilder customTemplatesDirectory(Path customTemplatesDirectory) {
        this.customTemplatesDirectory = customTemplatesDirectory;
        return this;
    }

    public GluonBuilder projectDirectory(Path projectDirectory) {
        this.projectDirectory = projectDirectory;
        return this;
    }

    public GluonBuilder entityDirectory(Path entityDirectory) {
        this.entityDirectory = entityDirectory;
        return this;
    }

    public GluonBuilder entityFormat(SpecFormat entityFormat) {
        this.entityFormat = entityFormat;
        return this;
    }

    public GluonBuilder entityFormat(String entityFormat) {
        this.entityFormat = SpecFormat.from(entityFormat);
        return this;
    }

    public GluonBuilder archetype(String archetype) {
        this.archetype = archetype;
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
        this.mockEntities = mockEntities;
        return this;
    }

    public Gluon createGluon() {
        try {
            validateBuild();
            setDefaults();
            final EntitySpecLoader entitySpecLoader = mockEntities == null
                    ? new JacksonEntitySpecLoader(entityDirectory, entityFormat)
                    : new MockEntitySpecLoader(mockEntities);
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
                    entitySpecLoader);
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
        if (entityDirectory == null && mockEntities == null) {
            // do not tell the client that it can mock entities, for avoiding strange messages in cli and maven plugin
            failedValidations.add("Entity directory is required");
        }
        if (templateExtension != null && customTemplatesDirectory == null) {
            failedValidations.add("Cannot specify template extension when using default templates (archetypes)");
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
                LOG.debug("Using Freemarker file template renderer from directory of custom templates: {}", customTemplatesDirectory.toAbsolutePath());
            } else {
                final String archetypeName = Objects.requireNonNullElse(archetype, DEFAULT_ARCHETYPE);
                final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
                templateManager = new GluonArchetypeTemplateManager(classLoader, archetypeName);
                LOG.debug("Using Freemarker resource template renderer for archetype " + archetypeName);
            }
        }
        if (entityFormat == null) {
            entityFormat = DEFAULT_FORMAT;
            LOG.debug("No entity format specified, using default: {}", entityFormat);
        }
        if (groupId == null) {
            groupId = DEFAULT_GROUP_ID;
            LOG.debug("No groupId specified, using default: {}", groupId);
        }
        if (artifactId == null) {
            artifactId = DEFAULT_ARTIFACT_ID;
            LOG.debug("No artifactId specified, using default: {}", artifactId);
        }
        if (version == null) {
            version = DEFAULT_VERSION;
            LOG.debug("No version specified, using default: {}", version);
        }
        if (projectDirectory == null) {
            // use artifactId relative path as default for generation directory
            projectDirectory = Paths.get(artifactId);
            LOG.debug("No project directory specified, using artifactId as relative path: {}", projectDirectory.toAbsolutePath());
        }
        if (friendlyName == null) {
            friendlyName = CaseUtils.hyphenSeparatedToDescriptive(artifactId);
            LOG.debug("No friendly name specified, using default parsed from artifact id: {}", friendlyName);
        }
        if (basePackage == null) {
            basePackage = groupId + "." + artifactId.replace("-", "");
            LOG.debug("No base package specified, using default from groupId and artifactId: {}", basePackage);
        }
        if (databaseVendor == null) {
            databaseVendor = DatabaseVendor.POSTGRESQL;
            LOG.debug("No database vendor specified, using default: {}", databaseVendor);
        }
        if (templateExtension == null) {
            templateExtension = DEFAULT_TEMPLATE_EXTENSION;
            LOG.debug("No template extension specified, using default: {}", templateExtension);
        }
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