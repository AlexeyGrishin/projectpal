package io.github.alexeygrishin.pal.ideaplugin.model.file;

import com.intellij.openapi.util.Key;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface PsiFileLocator {

    public boolean exists();

    @Nullable
    public PsiFile get();

    @NotNull
    public PsiFile createOrGet();

    public String getLanguage();

    public String getClassNameToReference();
}
