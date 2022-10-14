package io.tomrss.gluon.core.template;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public interface TemplateRenderer {
    // TODO i hate this method. because it causes the whole implementation to be mutable, clients could changes
    //  template directory causing re-instantiation in some impls causing MASSIVE performance overheads.
    //  in addition it is UGLY and MUST be removed in some way.
    //  The need for this is having single source of truth for templateBaseDirectory, that has to be hold by Gluon object
    void setTemplateBaseDirectory(Path templateBaseDirectory);

    void render(String templateName, Object model, Path outputFile) throws IOException;

    default void render(String templateName, Object model, String outputFile) throws IOException {
        render(templateName, model, Paths.get(outputFile));
    }

    default void render(String templateName, Object model, File outputFile) throws IOException {
        render(templateName, model, outputFile.toPath());
    }
}
