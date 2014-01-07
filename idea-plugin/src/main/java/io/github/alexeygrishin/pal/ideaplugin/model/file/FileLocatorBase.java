package io.github.alexeygrishin.pal.ideaplugin.model.file;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.impl.file.PsiDirectoryFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public abstract class FileLocatorBase implements PsiFileLocator {
    protected Project project;
    protected VirtualFile palFile = null;
    protected PsiManager psiManager;

    public FileLocatorBase(Project project) {
        this.project = project;
        psiManager = PsiManager.getInstance(project);
        palFile = getExistentPalFile(project);
    }

    private VirtualFile getPalFile() {
        if (palFile == null || !palFile.exists())
            palFile = getExistentPalFile(project);
        return palFile;
    }

    protected VirtualFile getExistentPalFile(Project project) {
        for (VirtualFile sourceRoot: getRootsForSource(project)) {
            VirtualFile palFile = sourceRoot.findFileByRelativePath(getDefaultFileLocation() + getFileName());
            if (palFile != null) return palFile;
        }
        return null;
    }

    protected VirtualFile[] getRootsForSource(Project project) {
        return ProjectRootManager.getInstance(project).getContentSourceRoots();
    }

    @Override
    public final boolean exists() {
        VirtualFile palFile = getPalFile();
        return palFile != null && palFile.exists();
    }

    @Nullable
    @Override
    public final PsiFile get() {
        VirtualFile palFile = getPalFile();

        return palFile == null ? null : psiManager.findFile(palFile);
    }

    @NotNull
    @Override
    public final PsiFile createOrGet() {
        //TODO: ugly
        VirtualFile palFile = getPalFile();
        if (palFile == null) {
            ApplicationManager.getApplication().invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    ApplicationManager.getApplication().runWriteAction(new Runnable() {
                        @Override
                        public void run() {
                            VirtualFile[] sourceRoots = getRootsForSource(project);
                            if (sourceRoots.length == 0)
                                throw new IllegalStateException("Cannot create Pal file in the project - there is no source root");
                            VirtualFile root = sourceRoots[0];
                            try {
                                VirtualFile palDirectory = VfsUtil.createDirectoryIfMissing(root, getDefaultFileLocation());
                                PsiDirectory dir = PsiDirectoryFactory.getInstance(project).createDirectory(palDirectory);
                                PsiFile palPsiFile = dir.createFile(getFileName());
                                FileLocatorBase.this.palFile = palPsiFile.getVirtualFile();

                            } catch (IOException e) {
                                throw new IllegalStateException("Cannot create directory for Pal class", e);
                            }
                        }
                    });
                }
            }, ModalityState.defaultModalityState());
        }
        return get();
    }


    protected abstract String getDefaultFileLocation();

    protected abstract String getFileName();


}
