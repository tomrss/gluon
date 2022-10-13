package io.tomrss.gluon.core.spec;

import java.util.ArrayList;
import java.util.List;

public class IndexSpec {
    public String name;
    public List<FieldSpec> columns = new ArrayList<>();
    public boolean unique = false;
    // TODO index could be also on relation...

    public IndexSpec(String name, List<FieldSpec> columns) {
        this(name, columns, false);
    }

    public IndexSpec(String name, List<FieldSpec> columns, boolean unique) {
        this.name = name;
        this.columns = columns;
        this.unique = unique;
    }
}
