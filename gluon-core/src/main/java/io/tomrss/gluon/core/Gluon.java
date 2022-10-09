package io.tomrss.gluon.core;

import io.tomrss.gluon.core.model.Entity;
import io.tomrss.gluon.core.model.ModelFactory;
import io.tomrss.gluon.core.model.config.EntityConfig;
import io.tomrss.gluon.core.template.TemplateRenderer;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class Gluon {

    public static final String DOMAIN_PACKAGE = "domain";
    public static final String RESOURCE_PACKAGE = "resource";
    public static final String SERVICE_PACKAGE = "service";

    private final ModelFactory modelFactory;
    private final TemplateRenderer templateRenderer;
    private final Path generatedProjectPath;
    private final String basePackage;
    private final Path srcDockerPath;
    private final Path srcResourcesPath;
    private final Path basePackagePath;
    private final String groupId;
    private final String artifactId;

    public Gluon(ModelFactory modelFactory,
                 TemplateRenderer templateRenderer,
                 Path generatedProjectPath,
                 String basePackage,
                 String groupId,
                 String artifactId) {
        this.modelFactory = modelFactory;
        this.templateRenderer = templateRenderer;
        this.generatedProjectPath = generatedProjectPath;
        this.basePackage = basePackage;
        this.srcDockerPath = Paths.get(generatedProjectPath.toString(), "src", "main", "docker");
        this.srcResourcesPath = Paths.get(generatedProjectPath.toString(), "src", "main", "resources");
        final Path srcJavaPath = Paths.get(generatedProjectPath.toString(), "src", "main", "java");
        this.basePackagePath = Paths.get(srcJavaPath.toString(), basePackage.split("\\."));
        this.groupId = groupId;
        this.artifactId = artifactId;
    }

    public void generateProject(List<EntityConfig> entityConfigList) throws IOException {
        final ModelFactory modelFactory = this.modelFactory;
        final List<Entity> entities = entityConfigList.stream()
                .map(modelFactory::buildEntity)
                .toList();

        copyRawFiles();
        generatePom();
        generateProjectStructure();
        generateDocker();
        generateJava(entities);
        generateResources(entities);
        generateTestJava(entities);
        generateTestResources();
    }

    private void generateProjectStructure() throws IOException {
        Files.createDirectories(srcDockerPath);
        Files.createDirectories(srcResourcesPath.resolve("liquibase").resolve("changes"));
        Files.createDirectories(basePackagePath);
        Files.createDirectory(basePackagePath.resolve(DOMAIN_PACKAGE));
        Files.createDirectory(basePackagePath.resolve(RESOURCE_PACKAGE));
        Files.createDirectory(basePackagePath.resolve(SERVICE_PACKAGE));
    }

    private void copyRawFiles() throws IOException {
        FileUtils.copyDirectory(new File("raw"), generatedProjectPath.toFile());
    }

    private void generatePom() throws IOException {
        final Map<String, Object> model = Map.of(
                "groupId", groupId,
                "artifactId", artifactId
        );
        templateRenderer.templateToFile("pom.xml.ftlh", model, generatedProjectPath.resolve("pom.xml"));
    }

    private void generateDocker() throws IOException {
        final Map<String, Object> model = Map.of(
                "artifactId", artifactId
        );
        template("Dockerfile.jvm.ftlh", model, srcDockerPath.resolve("Dockerfile.jvm"));
        template("Dockerfile.legacy-jar.ftlh", model, srcDockerPath.resolve("Dockerfile.legacy-jar"));
        template("Dockerfile.native.ftlh", model, srcDockerPath.resolve("Dockerfile.native"));
        template("Dockerfile.native-micro.ftlh", model, srcDockerPath.resolve("Dockerfile.native-micro"));
    }

    private void generateJava(List<Entity> entities) throws IOException {
        for (Entity entity : entities) {
            generateSourcesForEntity(entity);
        }
    }

    private void generateResources(List<Entity> entities) throws IOException {
        final Map<String, Object> model = Map.of(
                "groupId", groupId,
                "artifactId", artifactId,
                "entities", entities
        );
        final Path liquibasePath = srcResourcesPath.resolve("liquibase");
        template("application.properties.ftlh", model, srcResourcesPath.resolve("application.properties"));
        template("db-changelog-master.yaml.ftlh", model, liquibasePath.resolve("db-changelog-master.yaml"));
        template("init-database.yaml.ftlh", model, liquibasePath.resolve("changes").resolve("init-database.yaml"));
    }

    private void generateTestJava(List<Entity> entities) {
        // TODO
    }

    private void generateTestResources() {
        // TODO
    }

    private void generateSourcesForEntity(Entity entity) throws IOException {
        final Map<String, Object> model = Map.of(
                "basePackage", basePackage,
                "entity", entity
        );
        final String name = entity.getName();
        template("domain.java.ftlh", model, basePackagePath.resolve(DOMAIN_PACKAGE).resolve(name + ".java"));
        template("service.java.ftlh", model, basePackagePath.resolve(SERVICE_PACKAGE).resolve(name + "Service.java"));
        template("resource.java.ftlh", model, basePackagePath.resolve(RESOURCE_PACKAGE).resolve(name + "Resource.java"));
    }

    private void template(String templateName, Object model, Path path) throws IOException {
        templateRenderer.templateToFile(templateName, model, path);
    }
}
