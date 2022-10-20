package io.tomrss.gluon.core.model;

public class EntityTemplateModel {

    Project project;
    Entity entity;

    EntityTemplateModel() {
    }

    EntityTemplateModel(Project project, Entity entity) {
        this.project = project;
        this.entity = entity;
    }

    public Project getProject() {
        return project;
    }

    public Entity getEntity() {
        return entity;
    }
}
