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

    private final Map<String, Entity> entityCache = new ConcurrentHashMap<>();
    private final Map<String, Field> fieldCache = new ConcurrentHashMap<>();

    public ModelFactory(PhysicalNamingStrategy physicalNamingStrategy, SqlTypeTranslationStrategy sqlTypeTranslationStrategy) {
        this.physicalNamingStrategy = physicalNamingStrategy;
        this.sqlTypeTranslationStrategy = sqlTypeTranslationStrategy;
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
        entity.fields = entityConfig.fields
                .stream()
                .map(fieldConfig -> buildField(entity.name, fieldConfig))
                .collect(Collectors.toList());
        entity.indexes = entityConfig.indexes
                .stream()
                .map(indexConfig -> buildIndex(entity.name, indexConfig))
                .collect(Collectors.toList());
        entity.relations = entityConfig.relations
                .stream()
                .map(relationConfig -> buildRelation(entityConfig, relationConfig))
                .collect(Collectors.toList());
        return entity;
    }

    private Relation buildRelation(EntityConfig entityConfig, RelationConfig relationConfig) {
        final Relation relation = new Relation();
        relation.targetEntity = buildEntity(relationConfig.targetEntity);
        relation.joinColumn = physicalNamingStrategy.joinColumn(relationConfig);
        // TODO should be 2 different enums
        relation.type = relationConfig.type;
        // FIXME this is obviously wrong, i would like to add a "referencedField" in relation but adds too much hassle
        relation.inverseJoinColumn = physicalNamingStrategy.table(entityConfig) + "_id";
        relation.foreignKeyName = physicalNamingStrategy.foreignKey(relationConfig);
        return relation;
    }

    private Index buildIndex(String entityName, IndexConfig indexConfig) {
        final Index index = new Index();
        index.name = indexConfig.name;
        index.unique = indexConfig.unique;
        index.fields = indexConfig.columns
                .stream()
                .map(fieldConfig -> buildField(entityName, fieldConfig))
                .collect(Collectors.toList());
        return index;
    }

    public Field buildField(String entityName, FieldConfig fieldConfig) {
        return fieldCache.computeIfAbsent(entityName + CACHE_KEY_SEPARATOR + fieldConfig.name, key -> doBuildField(fieldConfig));
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
}
