package io.github.alexeygrishin.pal.ideaplugin.model.lang;

import com.intellij.openapi.project.Project;

public abstract class ProjectBasedLanguage implements LangAndPlatform {
    private Project project;

    public ProjectBasedLanguage(Project project) {
        this.project = project;
    }

    protected final Project getProject() {
        return project;
    }
}
