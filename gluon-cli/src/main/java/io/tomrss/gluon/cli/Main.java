package io.tomrss.gluon.cli;

import io.tomrss.gluon.core.Gluon;
import io.tomrss.gluon.core.GluonBuilder;
import io.tomrss.gluon.core.model.RelationType;
import io.tomrss.gluon.core.spec.EntitySpec;
import io.tomrss.gluon.core.spec.FieldSpec;
import io.tomrss.gluon.core.spec.IndexSpec;
import io.tomrss.gluon.core.spec.RelationSpec;
import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {

        // TODO temp code, just for trying gluon...

        final Path generatedProjectDirectory = Paths.get("../example");

        final Gluon gluon = new GluonBuilder()
                .generationDirectory(generatedProjectDirectory)
                .groupId("io.tomrss.gluon")
                .artifactId("example")
                .createGluon();

        FileUtils.deleteDirectory(generatedProjectDirectory.toFile());

        gluon.generateProject();
    }

    private static List<EntitySpec> mockEntities() {
        final EntitySpec role = new EntitySpec("Role",
                List.of(
                        new FieldSpec("name", String.class, false, true, 50),
                        new FieldSpec("enabled", Boolean.class, false, false),
                        new FieldSpec("assignableToUser", Boolean.class, false, false)
                ));
        final EntitySpec userSet = new EntitySpec("UserSet",
                List.of(
                        new FieldSpec("name", String.class, false, true, 50),
                        new FieldSpec("enabled", Boolean.class, false, false)
                ));
        final EntitySpec appUser = new EntitySpec("AppUser",
                List.of(
                        new FieldSpec("name", String.class, false, false, 50),
                        new FieldSpec("emailBlaBlaBla", String.class, false, false, 100),
                        new FieldSpec("description", String.class),
                        new FieldSpec("active", Boolean.class, false, false)
                ),
                List.of(
                        new RelationSpec("role", "Role", RelationType.MANY_TO_ONE, true, false),
                        new RelationSpec("userSets", "UserSet", RelationType.MANY_TO_MANY, false, true)
                ),
                List.of(
                        new IndexSpec("1", List.of("name", "emailBlaBlaBla"), true),
                        new IndexSpec("2", List.of("name"), false)
                ));
        return List.of(role, userSet, appUser);
    }
}
