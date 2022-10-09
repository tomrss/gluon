package io.tomrss.gluon.core.model.config;

import java.util.ArrayList;
import java.util.List;

public class IndexConfig {
    public String name;
    public List<FieldConfig> columns = new ArrayList<>();
    public boolean unique = false;
    // TODO index could be also on relation...

    public IndexConfig(String name, List<FieldConfig> columns) {
        this(name, columns, false);
    }

    public IndexConfig(String name, List<FieldConfig> columns, boolean unique) {
        this.name = name;
        this.columns = columns;
        this.unique = unique;
    }
}
