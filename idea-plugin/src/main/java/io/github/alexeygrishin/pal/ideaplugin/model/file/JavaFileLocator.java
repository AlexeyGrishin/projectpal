package io.github.alexeygrishin.pal.ideaplugin.model.file;

import com.intellij.lang.Language;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.vfs.*;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.impl.file.PsiDirectoryFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public class JavaFileLocator extends FileLocatorBase {

    private final static String PATH = "pal/";
    private final static String FILENAME = "Pal.java";

    public JavaFileLocator(Project project) {
        super(project);
    }

    @Override
    public String getLanguage() {
        return "java";
    }

    @Override
    public String getClassNameToReference() {
        return "Pal";
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
