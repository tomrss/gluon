package io.tomrss.gluon.core.model;

import io.tomrss.gluon.core.persistence.DatabaseVendor;
import io.tomrss.gluon.core.persistence.PhysicalNamingStrategy;
import io.tomrss.gluon.core.persistence.SqlTypeTranslationStrategy;
import io.tomrss.gluon.core.spec.EntitySpec;
import io.tomrss.gluon.core.spec.FieldSpec;
import io.tomrss.gluon.core.spec.IndexSpec;
import io.tomrss.gluon.core.spec.RelationSpec;

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

    public ModelFactory(DatabaseVendor databaseVendor) {
        this(databaseVendor.getPhysicalNamingStrategy(), databaseVendor.getSqlTypeTranslationStrategy());
    }

    public ModelFactory(PhysicalNamingStrategy physicalNamingStrategy, SqlTypeTranslationStrategy sqlTypeTranslationStrategy) {
        this.physicalNamingStrategy = physicalNamingStrategy;
        this.sqlTypeTranslationStrategy = sqlTypeTranslationStrategy;
        this.sqlTypeOfId = getSqlTypeOfId();
    }

    public Entity buildEntity(EntitySpec entitySpec) {
        return entityCache.computeIfAbsent(entitySpec.name, key -> doBuildEntity(entitySpec));
    }

    private Entity doBuildEntity(EntitySpec entitySpec) {
        final Entity entity = new Entity();
        entity.name = entitySpec.name;
        entity.table = physicalNamingStrategy.table(entitySpec);
        entity.sequence = physicalNamingStrategy.sequence(entitySpec);
        entity.sequenceGenerator = physicalNamingStrategy.sequenceGenerator(entitySpec);
        entity.primaryKeyName = physicalNamingStrategy.primaryKey(entitySpec);
        entity.resourcePath = physicalNamingStrategy.resourcePath(entitySpec);
        entity.idSqlType = sqlTypeOfId;
        entity.fields = entitySpec.fields
                .stream()
                .map(fieldSpec -> buildField(entitySpec, fieldSpec))
                .collect(Collectors.toList());
        entity.indexes = entitySpec.indexes
                .stream()
                .map(indexSpec -> buildIndex(entitySpec, indexSpec))
                .collect(Collectors.toList());
        entity.relations = entitySpec.relations
                .stream()
                .map(relationSpec -> buildRelation(entitySpec, relationSpec))
                .collect(Collectors.toList());
        return entity;
    }

    private Relation buildRelation(EntitySpec entitySpec, RelationSpec relationSpec) {
        final Relation relation = new Relation();
        relation.name = relationSpec.name;
        relation.targetEntity = buildEntity(relationSpec.targetEntity);
        relation.joinColumn = physicalNamingStrategy.joinColumn(relationSpec);
        relation.inverseJoinColumn = physicalNamingStrategy.inverseJoinColumn(entitySpec, relationSpec);
        relation.joinTable = physicalNamingStrategy.joinTable(entitySpec, relationSpec);
        // TODO should be 2 different enums
        relation.type = relationSpec.type;
        // FIXME this is obviously wrong, i would like to add a "referencedField" in relation but adds too much hassle
        relation.inverseJoinColumn = physicalNamingStrategy.table(entitySpec) + "_id";
        relation.foreignKeyName = physicalNamingStrategy.foreignKey(relationSpec);
        relation.nullable = relationSpec.nullable;
        relation.unique = relationSpec.unique;
        return relation;
    }

    private Index buildIndex(EntitySpec entitySpec, IndexSpec indexSpec) {
        final Index index = new Index();
        index.name = physicalNamingStrategy.index(entitySpec, indexSpec);
        index.unique = indexSpec.unique;
        index.fields = indexSpec.columns
                .stream()
                .map(fieldConfig -> buildField(entitySpec, fieldConfig))
                .collect(Collectors.toList());
        return index;
    }

    public Field buildField(EntitySpec entitySpec, FieldSpec fieldSpec) {
        return fieldCache.computeIfAbsent(entitySpec.name + CACHE_KEY_SEPARATOR + fieldSpec.name, key -> doBuildField(fieldSpec));
    }

    public Field doBuildField(FieldSpec fieldSpec) {
        final Field field = new Field();
        field.name = fieldSpec.name;
        field.type = fieldSpec.type;
        field.nullable = fieldSpec.nullable;
        field.unique = fieldSpec.unique;
        field.length = fieldSpec.length;
        field.column = physicalNamingStrategy.column(fieldSpec);
        field.sqlType = sqlTypeTranslationStrategy.sqlType(fieldSpec);
        return field;
    }

    private String getSqlTypeOfId() {
        final FieldSpec fakeIdField_justForTranslatingTheType = new FieldSpec();
        fakeIdField_justForTranslatingTheType.name = "id";
        fakeIdField_justForTranslatingTheType.type = Long.class;
        return sqlTypeTranslationStrategy.sqlType(fakeIdField_justForTranslatingTheType);
    }
}
