package io.tomrss.gluon.core.spec;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.dataformat.toml.TomlMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

import java.util.Arrays;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public enum SpecFormat implements Supplier<ObjectMapper> {
    JSON(JsonMapper::new),
    YAML(YAMLMapper::new),
    TOML(TomlMapper::new),
    XML(XmlMapper::new),
    ;

    private final Supplier<ObjectMapper> objectMapperFactory;

    SpecFormat(Supplier<ObjectMapper> objectMapperFactory) {
        this.objectMapperFactory = objectMapperFactory;
    }

    @Override
    public ObjectMapper get() {
        return objectMapperFactory.get();
    }

    public static SpecFormat from(String format) {
        return Arrays.stream(values())
                .filter(specFormat -> specFormat.name().equalsIgnoreCase(format))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("Format " + format + " unknown. " +
                        "Valid values are (case insensitive): " + Arrays.stream(values())
                        .map(SpecFormat::name)
                        .collect(Collectors.joining(", "))));
    }
}
