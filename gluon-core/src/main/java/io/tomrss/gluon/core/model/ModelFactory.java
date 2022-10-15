package io.tomrss.gluon.core.model;

import io.tomrss.gluon.core.persistence.PhysicalNamingStrategy;
import io.tomrss.gluon.core.persistence.SqlTypeTranslationStrategy;
import io.tomrss.gluon.core.spec.*;

import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ModelFactory {
    // TODO code in this class is far from clean, needs refactor

    // TODO handle defaults

    private final ProjectSpec projectSpec;
    private final PhysicalNamingStrategy physicalNamingStrategy;
    private final SqlTypeTranslationStrategy sqlTypeTranslationStrategy;
    private final String sqlTypeOfId;

    public ModelFactory(ProjectSpec projectSpec) {
        this.projectSpec = projectSpec;
        this.physicalNamingStrategy = projectSpec.databaseVendor().getPhysicalNamingStrategy();
        this.sqlTypeTranslationStrategy = projectSpec.databaseVendor().getSqlTypeTranslationStrategy();
        this.sqlTypeOfId = getSqlTypeOfId();
    }

    public TemplateModel buildModelForEntities(List<EntitySpec> entitySpecs) {
        final Project project = new Project();
        project.groupId = projectSpec.groupId();
        project.artifactId = projectSpec.artifactId();
        project.version = projectSpec.version();
        project.friendlyName = projectSpec.friendlyName();
        project.description = projectSpec.description();
        project.basePackage = projectSpec.basePackage();
        project.basePackagePath = Paths.get("", project.basePackage.split("\\.")).toString();
        project.imageRegistry = projectSpec.imageRegistry();
        project.dbVendor = projectSpec.databaseVendor();
        project.gluonVersion = getClass().getPackage().getImplementationVersion();
        if (project.gluonVersion == null) {
            project.gluonVersion = "0.0.0"; // just to be sure
        }

        // TODO list, list of map, map of list, maps passed as argument... AWFUL code, REFACTOR RIGHT NOW
        final List<Entity> entities = entitySpecs.stream()
                .map(this::buildEntity)
                .toList();
        final Map<String, List<RelationSpec>> relationSpecByEntityName = entitySpecs.stream()
                .collect(Collectors.toMap(EntitySpec::name, EntitySpec::relations));
        setEntityRelations(entities, relationSpecByEntityName);

        final TemplateModel templateModel = new TemplateModel();
        templateModel.entityModels = entities.stream()
                .map(entity -> new EntityTemplateModel(project, entity))
                .toList();
        templateModel.globalModel = new GlobalTemplateModel(project, entities);
        return templateModel;
    }

    private Entity buildEntity(EntitySpec entitySpec) {
        final Entity entity = new Entity();
        final String name = entitySpec.name();
        entity.name = name;
        entity.table = physicalNamingStrategy.table(name);
        entity.sequence = physicalNamingStrategy.sequence(name);
        entity.sequenceGenerator = physicalNamingStrategy.sequenceGenerator(name);
        entity.primaryKeyName = physicalNamingStrategy.primaryKey(name);
        entity.resourcePath = physicalNamingStrategy.resourcePath(name);
        entity.idSqlType = sqlTypeOfId;
        entity.fields = entitySpec.fields()
                .stream()
                .map(this::buildField)
                .toList();
        final Map<String, Field> fieldsByName = entity.fields.stream()
                .collect(Collectors.toMap(Field::getName, Function.identity()));
        entity.indexes = entitySpec.indexes()
                .stream()
                .map(indexSpec -> buildIndex(entitySpec.name(), indexSpec, fieldsByName))
                .toList();
        return entity;
    }

    private Index buildIndex(String entityName, IndexSpec indexSpec, Map<String, Field> fieldsByName) {
        final Index index = new Index();
        index.name = physicalNamingStrategy.index(entityName, indexSpec.name());
        index.unique = indexSpec.unique();
        index.fields = indexSpec.fields()
                .stream()
                .map(fieldName -> Optional.ofNullable(fieldsByName.get(fieldName))
                        .orElseThrow(() -> new ModelInitException("Unable to build index " + indexSpec.name() + ": " +
                                "field " + fieldName + " does not exist")))
                .toList();
        return index;
    }

    public Field buildField(FieldSpec fieldSpec) {
        final Field field = new Field();
        field.name = fieldSpec.name();
        field.type = fieldSpec.type();
        field.nullable = fieldSpec.nullable();
        field.unique = fieldSpec.unique();
        field.length = fieldSpec.length();
        field.column = physicalNamingStrategy.column(fieldSpec.name());
        field.sqlType = sqlTypeTranslationStrategy.sqlType(fieldSpec);
        return field;
    }

    private Relation buildRelation(String entityName, RelationSpec relationSpec, Map<String, Entity> entitiesByName) {
        final Entity targetEntity = entitiesByName.get(relationSpec.targetEntity());
        if (targetEntity == null) {
            throw new ModelInitException("Unable to build relation " + relationSpec.name() + ": " +
                    "target entity " + relationSpec.targetEntity() + " does not exist");
        }
        final Relation relation = new Relation();
        relation.name = relationSpec.name();
        relation.targetEntity = targetEntity;
        relation.joinColumn = physicalNamingStrategy.joinColumn(relationSpec.name());
        relation.inverseJoinColumn = physicalNamingStrategy.inverseJoinColumn(entityName);
        relation.joinTable = physicalNamingStrategy.joinTable(entityName, relationSpec.targetEntity());
        // TODO should be 2 different enums
        relation.type = relationSpec.type();
        relation.foreignKeyName = physicalNamingStrategy.foreignKey(relationSpec.name());
        relation.nullable = relationSpec.nullable();
        relation.unique = relationSpec.unique();
        return relation;
    }

    // TODO the signature of this method is a nightmare
    private void setEntityRelations(List<Entity> entities, Map<String, List<RelationSpec>> relationSpecByEntityName) {
        // TODO this is the type of code that makes me sick. i hate it
        //  the amount of bugs in this code must be mind boggling
        //  sorry, i wrote it during night
        final Map<String, Entity> entitiesByName = entities.stream()
                .collect(Collectors.toMap(Entity::getName, Function.identity()));
        for (Entity entity : entities) {
            final List<RelationSpec> relationSpecs = relationSpecByEntityName.get(entity.name);
            if (relationSpecs == null || relationSpecs.isEmpty()) {
                entity.relations = Collections.emptyList();
                continue;
            }
            entity.relations = relationSpecs.stream()
                    .map(relationSpec -> buildRelation(entity.name, relationSpec, entitiesByName))
                    .toList();
        }
    }

    private String getSqlTypeOfId() {
        final FieldSpec fakeIdFieldJustForTranslatingTheType = new FieldSpec("id", Long.class);
        return sqlTypeTranslationStrategy.sqlType(fakeIdFieldJustForTranslatingTheType);
    }
}
