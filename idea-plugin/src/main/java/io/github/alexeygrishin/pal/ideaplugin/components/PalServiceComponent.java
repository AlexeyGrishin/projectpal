package io.github.alexeygrishin.pal.ideaplugin.components;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.roots.impl.DirectoryIndex;
import com.intellij.openapi.startup.StartupManager;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VirtualFileAdapter;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.vfs.VirtualFileSystem;
import com.intellij.openapi.vfs.ex.VirtualFileManagerEx;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiManager;
import io.github.alexeygrishin.pal.ideaplugin.model.PalService;
import io.github.alexeygrishin.pal.ideaplugin.model.lang.BaseJava;
import io.github.alexeygrishin.pal.ideaplugin.model.lang.BaseRuby;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;

public class PalServiceComponent implements ProjectComponent {

    private Project project;

    public PalServiceComponent(Project project) throws Exception {
        this.project = project;
        PalService service = new PalService(project);
        project.putUserData(PalService.PAL_SERVICE, service);
        service.registerLangAndPlatform(BaseJava.class);
        service.registerLangAndPlatform(BaseRuby.class);

    }

    @Override
    public void projectOpened() {

    }

    @Override
    public void projectClosed() {

    }

    @Override
    public void initComponent() {

    }

    @Override
    public void disposeComponent() {

    }

    @NotNull
    @Override
    public String getComponentName() {
        return "pal";
    }
}
