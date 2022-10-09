package io.tomrss.gluon.core.template;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public interface TemplateRenderer {
    void templateToFile(String templateName, Object model, Path outputFile) throws IOException;

    default void templateToFile(String templateName, Object model, String outputFile) throws IOException {
        templateToFile(templateName, model, Paths.get(outputFile));
    }

    default void templateToFile(String templateName, Object model, File outputFile) throws IOException {
        templateToFile(templateName, model, outputFile.toPath());
    }
}
