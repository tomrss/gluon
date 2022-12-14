package io.tomrss.gluon.core.spec.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.tomrss.gluon.core.spec.EntitySpec;
import io.tomrss.gluon.core.spec.EntitySpecLoader;
import io.tomrss.gluon.core.spec.SpecFormat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

public class JacksonEntitySpecLoader implements EntitySpecLoader {

    private final Path entitiesDirectory;
    private final ObjectMapper objectMapper;

    public JacksonEntitySpecLoader(Path entitiesDirectory, SpecFormat specFormat) {
        this(entitiesDirectory, specFormat.get());
    }

    public JacksonEntitySpecLoader(Path entitiesDirectory, ObjectMapper objectMapper) {
        this.entitiesDirectory = entitiesDirectory;
        this.objectMapper = objectMapper;
    }

    @Override
    public List<EntitySpec> load() throws IOException {
        try (final Stream<Path> fileStream = Files.list(entitiesDirectory)) {
            return fileStream.map(this::readEntityFile).toList();
        }
    }

    private EntitySpec readEntityFile(Path file) {
        try {
            return objectMapper.readValue(file.toFile(), EntitySpec.class);
        } catch (IOException e) {
            throw new RuntimeException(e); // TODO
        }
    }
}
