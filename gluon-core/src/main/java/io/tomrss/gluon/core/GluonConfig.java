package io.tomrss.gluon.core;

import io.tomrss.gluon.core.model.RelationType;
import io.tomrss.gluon.core.model.config.EntityConfig;
import io.tomrss.gluon.core.model.config.FieldConfig;
import io.tomrss.gluon.core.model.config.IndexConfig;
import io.tomrss.gluon.core.model.config.RelationConfig;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class GluonConfig {

    private final Path entityConfigPath;

    public GluonConfig(Path entityConfigPath) {
        this.entityConfigPath = entityConfigPath;
    }

    public List<EntityConfig> loadEntitiesJson() throws IOException {
//        return new ObjectMapper()
//                .readValue(entityConfigPath.toFile(), Config.class)
//                .entities;

        final EntityConfig role = new EntityConfig("Role",
                List.of(
                        new FieldConfig("name", String.class, false, true, 50),
                        new FieldConfig("enabled", Boolean.class, false, false),
                        new FieldConfig("assignableToUser", Boolean.class, false, false)
                ));
        final FieldConfig userName = new FieldConfig("name", String.class, false, false, 50);
        final FieldConfig userEmail = new FieldConfig("email", String.class, false, false, 100);
        final EntityConfig appUser = new EntityConfig("AppUser",
                List.of(
                        userName,
                        userEmail,
                        new FieldConfig("description", String.class),
                        new FieldConfig("active", Boolean.class, false, false)
                ),
                List.of(
                        new RelationConfig("role", role, RelationType.MANY_TO_ONE)
                ),
                List.of(
                        new IndexConfig("idx_app_user_1", List.of(userName, userEmail), true)
                ));
        return List.of(role, appUser);
    }

    public static class Config {
        public List<EntityConfig> entities = new ArrayList<>();
    }
}
