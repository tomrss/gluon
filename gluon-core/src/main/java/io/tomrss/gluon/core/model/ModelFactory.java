package io.tomrss.gluon.core.model;

import io.tomrss.gluon.core.model.config.EntityConfig;
import io.tomrss.gluon.core.model.config.FieldConfig;
import io.tomrss.gluon.core.model.config.IndexConfig;
import io.tomrss.gluon.core.model.config.RelationConfig;
import io.tomrss.gluon.core.strategy.PhysicalNamingStrategy;
import io.tomrss.gluon.core.strategy.SqlTypeTranslationStrategy;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class ModelFactory {
    private static final String CACHE_KEY_SEPARATOR = "____###___";

    private final PhysicalNamingStrategy physicalNamingStrategy;
    private final SqlTypeTranslationStrategy sqlTypeTranslationStrategy;
    private final String sqlTypeOfId;

    private final Map<String, Entity> entityCache = new ConcurrentHashMap<>();
    private final Map<String, Field> fieldCache = new ConcurrentHashMap<>();

    public ModelFactory(PhysicalNamingStrategy physicalNamingStrategy, SqlTypeTranslationStrategy sqlTypeTranslationStrategy) {
        this.physicalNamingStrategy = physicalNamingStrategy;
        this.sqlTypeTranslationStrategy = sqlTypeTranslationStrategy;
        this.sqlTypeOfId = getSqlTypeOfId();
    }

    public Entity buildEntity(EntityConfig entityConfig) {
        return entityCache.computeIfAbsent(entityConfig.name, key -> doBuildEntity(entityConfig));
    }

    private Entity doBuildEntity(EntityConfig entityConfig) {
        final Entity entity = new Entity();
        entity.name = entityConfig.name;
        entity.table = physicalNamingStrategy.table(entityConfig);
        entity.sequence = physicalNamingStrategy.sequence(entityConfig);
        entity.sequenceGenerator = physicalNamingStrategy.sequenceGenerator(entityConfig);
        entity.primaryKeyName = physicalNamingStrategy.primaryKey(entityConfig);
        entity.resourcePath = physicalNamingStrategy.resourcePath(entityConfig);
        entity.idSqlType = sqlTypeOfId;
        entity.fields = entityConfig.fields
                .stream()
                .map(fieldConfig -> buildField(entityConfig, fieldConfig))
                .collect(Collectors.toList());
        entity.indexes = entityConfig.indexes
                .stream()
                .map(indexConfig -> buildIndex(entityConfig, indexConfig))
                .collect(Collectors.toList());
        entity.relations = entityConfig.relations
                .stream()
                .map(relationConfig -> buildRelation(entityConfig, relationConfig))
                .collect(Collectors.toList());
        return entity;
    }

    private Relation buildRelation(EntityConfig entityConfig, RelationConfig relationConfig) {
        final Relation relation = new Relation();
        relation.name = relationConfig.name;
        relation.targetEntity = buildEntity(relationConfig.targetEntity);
        relation.joinColumn = physicalNamingStrategy.joinColumn(relationConfig);
        relation.inverseJoinColumn = physicalNamingStrategy.inverseJoinColumn(entityConfig, relationConfig);
        relation.joinTable = physicalNamingStrategy.joinTable(entityConfig, relationConfig);
        // TODO should be 2 different enums
        relation.type = relationConfig.type;
        // FIXME this is obviously wrong, i would like to add a "referencedField" in relation but adds too much hassle
        relation.inverseJoinColumn = physicalNamingStrategy.table(entityConfig) + "_id";
        relation.foreignKeyName = physicalNamingStrategy.foreignKey(relationConfig);
        relation.nullable = relationConfig.nullable;
        relation.unique = relationConfig.unique;
        return relation;
    }

    private Index buildIndex(EntityConfig entityConfig, IndexConfig indexConfig) {
        final Index index = new Index();
        index.name = physicalNamingStrategy.index(entityConfig, indexConfig);
        index.unique = indexConfig.unique;
        index.fields = indexConfig.columns
                .stream()
                .map(fieldConfig -> buildField(entityConfig, fieldConfig))
                .collect(Collectors.toList());
        return index;
    }

    public Field buildField(EntityConfig entityConfig, FieldConfig fieldConfig) {
        return fieldCache.computeIfAbsent(entityConfig.name + CACHE_KEY_SEPARATOR + fieldConfig.name, key -> doBuildField(fieldConfig));
    }

    public Field doBuildField(FieldConfig fieldConfig) {
        final Field field = new Field();
        field.name = fieldConfig.name;
        field.type = fieldConfig.type;
        field.nullable = fieldConfig.nullable;
        field.unique = fieldConfig.unique;
        field.length = fieldConfig.length;
        field.column = physicalNamingStrategy.column(fieldConfig);
        field.sqlType = sqlTypeTranslationStrategy.sqlType(fieldConfig);
        return field;
    }

    private String getSqlTypeOfId() {
        final FieldConfig fakeIdField_justForTranslatingTheType = new FieldConfig();
        fakeIdField_justForTranslatingTheType.name = "id";
        fakeIdField_justForTranslatingTheType.type = Long.class;
        return sqlTypeTranslationStrategy.sqlType(fakeIdField_justForTranslatingTheType);
    }
}
