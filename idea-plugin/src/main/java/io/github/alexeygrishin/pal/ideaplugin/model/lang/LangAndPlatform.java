package io.github.alexeygrishin.pal.ideaplugin.model.lang;

import com.intellij.lang.Language;
import io.github.alexeygrishin.pal.ideaplugin.model.file.PsiFileLocator;

public interface LangAndPlatform {

    public boolean matches(Language language);

    public PsiFileLocator getFileLocator();

    public String getPalLanguage();

    public FunctionCallString getFunctionCallString(String functionName);

}
