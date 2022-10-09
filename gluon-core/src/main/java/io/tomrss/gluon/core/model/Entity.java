package io.tomrss.gluon.core.model;

import java.util.List;

public class Entity {

    Entity() {
    }

    String name;
    String table;
    String sequence;
    String sequenceGenerator;
    String resourcePath;
    String primaryKeyName;
    List<Field> fields;
    List<Relation> relations;
    List<Index> indexes;

    public String getName() {
        return name;
    }

    public String getTable() {
        return table;
    }

    public String getSequence() {
        return sequence;
    }

    public String getSequenceGenerator() {
        return sequenceGenerator;
    }

    public String getResourcePath() {
        return resourcePath;
    }

    public String getPrimaryKeyName() {
        return primaryKeyName;
    }

    public List<Field> getFields() {
        return fields;
    }

    public List<Relation> getRelations() {
        return relations;
    }

    public List<Index> getIndexes() {
        return indexes;
    }
}
