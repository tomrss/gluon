package io.tomrss.gluon.core.template;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public interface FileTemplateRenderer {
    void renderFileTemplate(String templateName, Object model, Path outputFile) throws IOException;

    default void renderFileTemplate(String templateName, Object model, String outputFile) throws IOException {
        renderFileTemplate(templateName, model, Paths.get(outputFile));
    }

    default void renderFileTemplate(String templateName, Object model, File outputFile) throws IOException {
        renderFileTemplate(templateName, model, outputFile.toPath());
    }
}
