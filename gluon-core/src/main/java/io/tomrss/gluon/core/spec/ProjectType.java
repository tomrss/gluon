package io.tomrss.gluon.core.spec;

import java.util.Arrays;
import java.util.stream.Collectors;

public enum ProjectType {
    MAVEN,
    PYTHON,
    ;

    public static ProjectType from(String projectType) {
        return Arrays.stream(values())
                .filter(it -> it.name().equalsIgnoreCase(projectType))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("Project type " + projectType + " unknown. " +
                        "Valid values are (case insensitive): " + Arrays.stream(values())
                        .map(ProjectType::name)
                        .collect(Collectors.joining(", "))));
    }
}
