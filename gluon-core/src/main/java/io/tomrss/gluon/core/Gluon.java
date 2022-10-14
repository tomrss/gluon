package io.tomrss.gluon.core;

import io.tomrss.gluon.core.model.*;
import io.tomrss.gluon.core.persistence.DatabaseVendor;
import io.tomrss.gluon.core.spec.EntitySpec;
import io.tomrss.gluon.core.spec.EntitySpecReader;
import io.tomrss.gluon.core.spec.ProjectSpec;
import io.tomrss.gluon.core.template.TemplateRenderer;
import org.apache.commons.io.FileExistsException;
import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Gluon {
    public static final String DOMAIN_PACKAGE = "domain";
    public static final String RESOURCE_PACKAGE = "resource";
    public static final String SERVICE_PACKAGE = "service";

    private final TemplateRenderer templateRenderer;
    private final DatabaseVendor databaseVendor;
    private final Path generatedProjectPath;
    private final Path rawFilesDirectory;
    private final String basePackage;
    private final Path srcDockerPath;
    private final Path srcResourcesPath;
    private final EntitySpecReader entitySpecReader;
    private final Path basePackagePath;
    private final String groupId;
    private final String artifactId;

    Gluon(TemplateRenderer templateRenderer,
                 DatabaseVendor databaseVendor,
                 Path generatedProjectPath,
                 Path rawFilesDirectory,
                 String basePackage,
                 String groupId,
                 String artifactId,
                 EntitySpecReader entitySpecReader) {
        // TODO just have a project spec here built from builder
        this.templateRenderer = templateRenderer;
        this.databaseVendor = databaseVendor;
        this.generatedProjectPath = generatedProjectPath;
        this.rawFilesDirectory = rawFilesDirectory;
        this.basePackage = basePackage;
        this.srcDockerPath = Paths.get(generatedProjectPath.toString(), "src", "main", "docker");
        this.srcResourcesPath = Paths.get(generatedProjectPath.toString(), "src", "main", "resources");
        this.entitySpecReader = entitySpecReader;
        final Path srcJavaPath = Paths.get(generatedProjectPath.toString(), "src", "main", "java");
        this.basePackagePath = Paths.get(srcJavaPath.toString(), basePackage.split("\\."));
        this.groupId = groupId;
        this.artifactId = artifactId;
    }

    public void generateProject() throws IOException {
        if (Files.exists(generatedProjectPath)) {
            throw new FileExistsException("Directory of generated project already exists: " + generatedProjectPath.toAbsolutePath());
        }
        Files.createDirectory(generatedProjectPath);

        final List<EntitySpec> entitySpecs = entitySpecReader.read();

        final ModelFactory modelFactory = new ModelFactory(new ProjectSpec(groupId, artifactId, basePackage, databaseVendor));
        final TemplateModel templateModel = modelFactory.buildModelForEntities(entitySpecs);

        copyRawFiles();
        generateStructuralSources(templateModel.getStructuralModel());
        generateGlobalSources(templateModel.getGlobalModel());
        generateEntitySources(templateModel.getEntityModels());
    }

    private void copyRawFiles() throws IOException {
        FileUtils.copyDirectory(rawFilesDirectory.toFile(), generatedProjectPath.toFile());
    }

    private void generateStructuralSources(StructuralTemplateModel model) throws IOException {
        template("pom.xml.ftlh", model, generatedProjectPath.resolve("pom.xml"));
        template("Dockerfile.jvm.ftlh", model, srcDockerPath.resolve("Dockerfile.jvm"));
        template("Dockerfile.legacy-jar.ftlh", model, srcDockerPath.resolve("Dockerfile.legacy-jar"));
        template("Dockerfile.native.ftlh", model, srcDockerPath.resolve("Dockerfile.native"));
        template("Dockerfile.native-micro.ftlh", model, srcDockerPath.resolve("Dockerfile.native-micro"));
    }

    private void generateGlobalSources(GlobalTemplateModel model) throws IOException {
        final Path liquibasePath = srcResourcesPath.resolve("liquibase");
        template("application.properties.ftlh", model, srcResourcesPath.resolve("application.properties"));
        template("db-changelog-master.yml.ftlh", model, liquibasePath.resolve("db-changelog-master.yml"));
        template("init-database.yml.ftlh", model, liquibasePath.resolve("changes").resolve("init-database.yml"));
    }

    private void generateEntitySources(List<EntityTemplateModel> entityModels) throws IOException {
        for (EntityTemplateModel model : entityModels) {
            final String name = model.getEntity().getName();
            template("domain.java.ftlh", model, basePackagePath.resolve(DOMAIN_PACKAGE).resolve(name + ".java"));
            template("service.java.ftlh", model, basePackagePath.resolve(SERVICE_PACKAGE).resolve(name + "Service.java"));
            template("resource.java.ftlh", model, basePackagePath.resolve(RESOURCE_PACKAGE).resolve(name + "Resource.java"));
        }
    }

    private void template(String templateName, Object model, Path path) throws IOException {
        templateRenderer.templateToFile(templateName, model, path);
    }
}
