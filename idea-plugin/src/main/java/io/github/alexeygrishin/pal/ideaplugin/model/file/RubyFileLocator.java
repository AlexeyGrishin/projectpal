package io.github.alexeygrishin.pal.ideaplugin.model.file;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RubyFileLocator extends FileLocatorBase {

    public RubyFileLocator(Project project) {
        super(project);
    }

    @Override
    protected VirtualFile[] getRootsForSource(Project project) {
        return ProjectRootManager.getInstance(project).getContentRoots();
    }

    @Override
    protected String getDefaultFileLocation() {
        return "pal/";
    }

    @Override
    protected String getFileName() {
        return "pal.rb";
    }

    @Override
    public String getLanguage() {
        return "ruby";
    }

    @Override
    public String getClassNameToReference() {
        return "Pal";
    }
}
