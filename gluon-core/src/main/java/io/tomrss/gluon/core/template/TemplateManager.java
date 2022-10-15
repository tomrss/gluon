package io.tomrss.gluon.core.template;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public interface TemplateManager {

    List<String> listTemplates(String templateExtension) throws IOException;

    void render(String templateName, Object model, Path outputFile) throws IOException;

    default void render(String templateName, Object model, String outputFile) throws IOException {
        render(templateName, model, Paths.get(outputFile));
    }

    default void render(String templateName, Object model, File outputFile) throws IOException {
        render(templateName, model, outputFile.toPath());
    }
}
