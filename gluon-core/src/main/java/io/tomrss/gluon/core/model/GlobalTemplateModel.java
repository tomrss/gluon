package io.tomrss.gluon.core.model;

import java.util.List;

public class GlobalTemplateModel {

    Project project;
    List<Entity> entities;

    GlobalTemplateModel() {
    }

    GlobalTemplateModel(Project project, List<Entity> entities) {
        this.project = project;
        this.entities = entities;
    }

    public Project getProject() {
        return project;
    }

    public List<Entity> getEntities() {
        return entities;
    }
}
