package io.github.alexeygrishin.pal.ideaplugin.model.lang;

import com.intellij.lang.Language;
import com.intellij.openapi.project.Project;
import io.github.alexeygrishin.pal.ideaplugin.model.file.JavaFileLocator;
import io.github.alexeygrishin.pal.ideaplugin.model.file.PsiFileLocator;

public class BaseJava extends ProjectBasedLanguage {

    public BaseJava(Project project) {
        super(project);
    }

    @Override
    public boolean matches(Language language) {
        return language.getID().equalsIgnoreCase("java");
    }

    @Override
    public PsiFileLocator getFileLocator() {
        return new JavaFileLocator(getProject());
    }

    @Override
    public String getPalLanguage() {
        return "java";
    }

    @Override
    public FunctionCallString getFunctionCallString(String functionName) {
        return new FunctionCallString("Pal." + functionName + "()", -1);
    }
}
