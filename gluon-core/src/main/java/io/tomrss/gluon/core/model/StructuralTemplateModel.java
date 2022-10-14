package io.tomrss.gluon.core.model;

public class StructuralTemplateModel {

    Project project;

    StructuralTemplateModel() {
    }

    StructuralTemplateModel(Project project) {
        this.project = project;
    }

    public Project getProject() {
        return project;
    }
}
