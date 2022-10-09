package io.tomrss.gluon.core.model;

import java.util.ArrayList;
import java.util.List;

public class Index {
    Index() {
    }

    String name;
    List<Field> fields = new ArrayList<>();
    boolean unique;

    public String getName() {
        return name;
    }

    public List<Field> getFields() {
        return fields;
    }

    public boolean isUnique() {
        return unique;
    }
}
