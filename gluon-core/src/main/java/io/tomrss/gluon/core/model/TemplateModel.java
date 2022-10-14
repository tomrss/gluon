package io.tomrss.gluon.core.model;

import java.util.List;

public class TemplateModel {
    StructuralTemplateModel structuralModel;
    GlobalTemplateModel globalModel;
    List<EntityTemplateModel> entityModels;

    TemplateModel() {
    }

    public StructuralTemplateModel getStructuralModel() {
        return structuralModel;
    }

    public GlobalTemplateModel getGlobalModel() {
        return globalModel;
    }

    public List<EntityTemplateModel> getEntityModels() {
        return entityModels;
    }
}
