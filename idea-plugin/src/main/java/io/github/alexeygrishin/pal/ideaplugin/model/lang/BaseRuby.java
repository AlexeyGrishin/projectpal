package io.github.alexeygrishin.pal.ideaplugin.model.lang;

import com.intellij.lang.Language;
import com.intellij.openapi.project.Project;
import io.github.alexeygrishin.pal.ideaplugin.model.file.PsiFileLocator;
import io.github.alexeygrishin.pal.ideaplugin.model.file.RubyFileLocator;
import io.github.alexeygrishin.pal.tools.Tool;

public class BaseRuby extends ProjectBasedLanguage {
    public BaseRuby(Project project) {
        super(project);
    }

    @Override
    public boolean matches(Language language) {
        return language.getID().equalsIgnoreCase("ruby");
    }

    @Override
    public PsiFileLocator getFileLocator() {
        return new RubyFileLocator(getProject());
    }

    @Override
    public String getPalLanguage() {
        return "ruby";
    }

    @Override
    public FunctionCallString getFunctionCallString(String functionName) {
        return new FunctionCallString("Pal." + Tool.toUnderscore(functionName) + "()", -1);
    }
}
