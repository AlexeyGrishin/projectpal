package io.github.alexeygrishin.pal.ideaplugin.model.file;

import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Provides information about Pal file location depending on language (java, ruby, etc.) and platform (ruby on rails, jse, jee, etc.)
 */
public interface PsiFileLocator {

    /**
     *
     * @return true if pal class physically exists
     */
    public boolean exists();

    /**
     *
     * @return pal class file if exists or null if not
     */
    @Nullable
    public PsiFile get();

    /**
     * Creates the pal class file (if not exists), waits for creation and only then returns.
     * If file already exists then returns immediately.
     */
    public void createSync();

    /**
     *
     * @return file path (to show to user)
     */
    public @NotNull String getPathAsString();
}
