package io.tomrss.gluon.core.model;

import io.tomrss.gluon.core.persistence.PhysicalNamingStrategy;
import io.tomrss.gluon.core.persistence.SqlTypeTranslationStrategy;
import io.tomrss.gluon.core.spec.*;

import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ModelFactory {
    private static final String CACHE_KEY_SEPARATOR = "____###___";

    private final PhysicalNamingStrategy physicalNamingStrategy;
    private final SqlTypeTranslationStrategy sqlTypeTranslationStrategy;
    private final String sqlTypeOfId;

    private final Map<String, Entity> entityCache = new ConcurrentHashMap<>();
    private final Map<String, Field> fieldCache = new ConcurrentHashMap<>();
    private final ProjectSpec projectSpec;

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
        project.basePackage = projectSpec.basePackage();
        project.basePackagePath = Paths.get("", project.basePackage.split("\\.")).toString();
        project.dbVendor = projectSpec.databaseVendor();

        final List<Entity> entities = entitySpecs.stream()
                .map(this::buildEntity)
                .toList();


        final TemplateModel templateModel = new TemplateModel();
        templateModel.entityModels = entities.stream()
                .map(entity -> new EntityTemplateModel(project, entity))
                .toList();
        templateModel.globalModel = new GlobalTemplateModel(project, entities);
        return templateModel;
    }

    public Entity buildEntity(EntitySpec entitySpec) {
        return entityCache.computeIfAbsent(entitySpec.name(), key -> doBuildEntity(entitySpec));
    }

    private Entity doBuildEntity(EntitySpec entitySpec) {
        final Entity entity = new Entity();
        entity.name = entitySpec.name();
        entity.table = physicalNamingStrategy.table(entitySpec);
        entity.sequence = physicalNamingStrategy.sequence(entitySpec);
        entity.sequenceGenerator = physicalNamingStrategy.sequenceGenerator(entitySpec);
        entity.primaryKeyName = physicalNamingStrategy.primaryKey(entitySpec);
        entity.resourcePath = physicalNamingStrategy.resourcePath(entitySpec);
        entity.idSqlType = sqlTypeOfId;
        entity.fields = entitySpec.fields()
                .stream()
                .map(fieldSpec -> buildField(entitySpec, fieldSpec))
                .toList();
        entity.indexes = entitySpec.indexes()
                .stream()
                .map(indexSpec -> buildIndex(entitySpec, indexSpec))
                .toList();
        entity.relations = entitySpec.relations()
                .stream()
                .map(relationSpec -> buildRelation(entitySpec, relationSpec))
                .toList();
        return entity;
    }

    private Relation buildRelation(EntitySpec entitySpec, RelationSpec relationSpec) {
        final Relation relation = new Relation();
        relation.name = relationSpec.name();
        relation.targetEntity = buildEntity(relationSpec.targetEntity());
        relation.joinColumn = physicalNamingStrategy.joinColumn(relationSpec);
        relation.inverseJoinColumn = physicalNamingStrategy.inverseJoinColumn(entitySpec, relationSpec);
        relation.joinTable = physicalNamingStrategy.joinTable(entitySpec, relationSpec);
        // TODO should be 2 different enums
        relation.type = relationSpec.type();
        relation.foreignKeyName = physicalNamingStrategy.foreignKey(relationSpec);
        relation.nullable = relationSpec.nullable();
        relation.unique = relationSpec.unique();
        return relation;
    }

    private Index buildIndex(EntitySpec entitySpec, IndexSpec indexSpec) {
        final Index index = new Index();
        index.name = physicalNamingStrategy.index(entitySpec, indexSpec);
        index.unique = indexSpec.unique();
        index.fields = indexSpec.columns()
                .stream()
                .map(fieldConfig -> buildField(entitySpec, fieldConfig))
                .toList();
        return index;
    }

    public Field buildField(EntitySpec entitySpec, FieldSpec fieldSpec) {
        return fieldCache.computeIfAbsent(entitySpec.name() + CACHE_KEY_SEPARATOR + fieldSpec.name(), key -> doBuildField(fieldSpec));
    }

    public Field doBuildField(FieldSpec fieldSpec) {
        final Field field = new Field();
        field.name = fieldSpec.name();
        field.type = fieldSpec.type();
        field.nullable = fieldSpec.nullable();
        field.unique = fieldSpec.unique();
        field.length = fieldSpec.length();
        field.column = physicalNamingStrategy.column(fieldSpec);
        field.sqlType = sqlTypeTranslationStrategy.sqlType(fieldSpec);
        return field;
    }

    private String getSqlTypeOfId() {
        final FieldSpec fakeIdFieldJustForTranslatingTheType = new FieldSpec("id", Long.class);
        return sqlTypeTranslationStrategy.sqlType(fakeIdFieldJustForTranslatingTheType);
    }
}
