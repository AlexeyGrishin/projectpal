package io.github.alexeygrishin.pal.ideaplugin.model.file;

import com.intellij.openapi.project.Project;

public class JavaFileLocator extends FileLocatorBase {

    private final static String PATH = "pal/";
    private final static String FILENAME = "Pal.java";

    public JavaFileLocator(Project project) {
        super(project);
    }

    @Override
    protected String getDefaultFileLocation() {
        return PATH;
    }

    @Override
    protected String getFileName() {
        return FILENAME;
    }
}
