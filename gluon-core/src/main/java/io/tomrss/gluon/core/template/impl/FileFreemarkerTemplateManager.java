package io.tomrss.gluon.core.template.impl;

import freemarker.cache.FileTemplateLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

public class FileFreemarkerTemplateManager extends FreemarkerTemplateManager {

    private final Path templateBaseDirectory;

    public FileFreemarkerTemplateManager(Path templateBaseDirectory) throws IOException {
        super(new FileTemplateLoader(templateBaseDirectory.toFile()));
        this.templateBaseDirectory = templateBaseDirectory;
    }

    @Override
    public List<String> listTemplates(String templateExtension) throws IOException {
        try (final Stream<Path> templates = Files.walk(templateBaseDirectory)) {
            return templates
                    .filter(Files::isRegularFile)
                    .filter(f -> f.getFileName().toString().endsWith(templateExtension))
                    .map(templateBaseDirectory::relativize)
                    .map(Path::toString)
                    .toList();
        }
    }
}
