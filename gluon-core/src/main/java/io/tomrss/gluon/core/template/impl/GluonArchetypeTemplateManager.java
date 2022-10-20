package io.tomrss.gluon.core.template.impl;

import freemarker.cache.ClassTemplateLoader;
import io.tomrss.gluon.core.util.ResourceUtils;

import java.io.IOException;
import java.util.List;

public class GluonArchetypeTemplateManager extends FreemarkerTemplateManager {

    public static final String ARCHETYPE_BASE_PATH = "archetypes/";
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
        return ResourceUtils.readMetadataFile(classLoader, metadataResourceName);
    }
}
