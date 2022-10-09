package io.tomrss.gluon.cli;

import io.tomrss.gluon.core.Gluon;
import io.tomrss.gluon.core.GluonConfig;
import io.tomrss.gluon.core.model.ModelFactory;
import io.tomrss.gluon.core.model.config.EntityConfig;
import io.tomrss.gluon.core.strategy.impl.PostgresTypeTranslationStrategy;
import io.tomrss.gluon.core.strategy.impl.SnakeCaseNamingStrategy;
import io.tomrss.gluon.core.template.impl.FreemarkerTemplateRenderer;
import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {

        // TODO temp code, just for trying gluon...

        final Path generatedProjectPath = Paths.get("../example-gluon");
        final Gluon gluon = new Gluon(
                new ModelFactory(new SnakeCaseNamingStrategy(), new PostgresTypeTranslationStrategy()),
                new FreemarkerTemplateRenderer("templates"),
                generatedProjectPath,
                "io.tomrss.example.gluon",
                "io.tomrss",
                "example-gluon"
        );

        final List<EntityConfig> entities = new GluonConfig(Paths.get("config/entities.json")).loadEntitiesJson();

        FileUtils.deleteDirectory(generatedProjectPath.toFile());
        gluon.generateProject(entities);
    }
}
