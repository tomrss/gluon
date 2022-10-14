package io.tomrss.gluon.core.model;

import java.util.List;

public class TemplateModel {
    GlobalTemplateModel globalModel;
    List<EntityTemplateModel> entityModels;

    TemplateModel() {
    }

    public GlobalTemplateModel getGlobalModel() {
        return globalModel;
    }

    public List<EntityTemplateModel> getEntityModels() {
        return entityModels;
    }
}
