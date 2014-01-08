package io.github.alexeygrishin.pal.ideaplugin.model.lang;

import com.intellij.lang.Language;
import io.github.alexeygrishin.pal.ideaplugin.model.file.PsiFileLocator;

/**
 * To use inside PalService
 */
public interface LangAndPlatformEx extends LangAndPlatform {

    public boolean matches(Language language);

    public PsiFileLocator getFileLocator();

}
