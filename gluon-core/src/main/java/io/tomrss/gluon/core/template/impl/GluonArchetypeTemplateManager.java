package io.tomrss.gluon.core.template.impl;

import freemarker.cache.ClassTemplateLoader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Objects;

public class GluonArchetypeTemplateManager extends FreemarkerTemplateManager {

    private final ClassLoader classLoader;
    private final String archetypeName;

    public GluonArchetypeTemplateManager(ClassLoader classLoader, String archetypeName) {
        super(new ClassTemplateLoader(classLoader, archetypeName));
        this.classLoader = classLoader;
        this.archetypeName = archetypeName;
    }

    @Override
    public List<String> listTemplates(String templateExtension) throws IOException {
        try (final InputStream is = classLoader.getResourceAsStream(archetypeName + "/archetype-metadata.txt")) {
            if (is == null) {
                throw new IllegalStateException("Missing metadata file for archetype " + archetypeName +
                        ", add archetype-metadata.txt file to archetype directory containing newline separated names of every template used in archetype");
            }
            try (final InputStreamReader isr = new InputStreamReader(is);
                 final BufferedReader reader = new BufferedReader(isr)) {
                return reader.lines()
                        .filter(Objects::nonNull)
                        .map(String::trim)
                        .filter(line -> line.length() > 0)
                        .filter(line -> !line.startsWith("#"))
                        .filter(line -> line.endsWith(".gluon"))
                        .toList();
            }
        }
    }
}
