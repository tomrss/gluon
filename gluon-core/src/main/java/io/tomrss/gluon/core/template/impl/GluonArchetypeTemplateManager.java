package io.tomrss.gluon.core.template.impl;

import freemarker.cache.ClassTemplateLoader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Objects;

public class GluonArchetypeTemplateManager extends FreemarkerTemplateManager {

    public static final String ARCHETYPE_BASE_PATH = "archetypes/";
    public static final String ARCHETYPE_METADATA_COMMENT_MARKER = "#";
    public static final String ARCHETYPE_METADATA_FILENAME = "archetype-metadata.txt";

    private final ClassLoader classLoader;
    private final String archetypeName;

    public GluonArchetypeTemplateManager(ClassLoader classLoader, String archetypeName) {
        super(new ClassTemplateLoader(classLoader, ARCHETYPE_BASE_PATH + archetypeName));
        this.classLoader = classLoader;
        this.archetypeName = archetypeName;
        if (classLoader.getResource(ARCHETYPE_BASE_PATH + archetypeName) == null) {
            throw new IllegalArgumentException("No archetype named " + archetypeName + " found");
        }
    }

    @Override
    public List<String> listTemplates(String templateExtension) throws IOException {
        final String metadataResourceName = ARCHETYPE_BASE_PATH + archetypeName + "/" + ARCHETYPE_METADATA_FILENAME;
        try (final InputStream is = classLoader.getResourceAsStream(metadataResourceName)) {
            if (is == null) {
                throw new IllegalStateException("Missing metadata file for archetype " + archetypeName +
                        ", add " + ARCHETYPE_METADATA_FILENAME + " file to archetype directory " +
                        "containing newline separated names of every template used in archetype");
            }
            try (final InputStreamReader isr = new InputStreamReader(is);
                 final BufferedReader reader = new BufferedReader(isr)) {
                return reader.lines()
                        .filter(Objects::nonNull)
                        .map(String::trim)
                        .filter(line -> line.length() > 0)
                        .filter(line -> !line.startsWith(ARCHETYPE_METADATA_COMMENT_MARKER))
                        .filter(line -> line.endsWith(templateExtension))
                        .toList();
            }
        }
    }
}
