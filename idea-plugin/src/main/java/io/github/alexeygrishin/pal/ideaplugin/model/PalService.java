package io.github.alexeygrishin.pal.ideaplugin.model;

import com.intellij.lang.Language;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import io.github.alexeygrishin.pal.api.PalFunction;
import io.github.alexeygrishin.pal.ideaplugin.model.file.IdeaFile;
import io.github.alexeygrishin.pal.ideaplugin.model.lang.FunctionCallString;
import io.github.alexeygrishin.pal.ideaplugin.model.lang.LangAndPlatform;
import io.github.alexeygrishin.pal.ideaplugin.model.lang.ProjectBasedLanguage;
import io.github.alexeygrishin.pal.ideaplugin.remote.PalServer;
import io.github.alexeygrishin.pal.ideaplugin.remote.PalServerListener;
import io.github.alexeygrishin.tools.Listenable;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;

public class PalService extends Listenable<PalServiceListener> {
    private PalClass palClass;
    private PalServer server;
    private List<LangAndPlatform> langsAndPlatforms = new LinkedList<LangAndPlatform>();
    private Project project;

    public PalService(Project project) {
        this.project = project;
        initListeners(PalServiceListener.class);
        server = PalServer.getInstance();
    }

    public PalClass getPalClass() {
        return palClass;
    }

    public PalClass getPalClass(Language language) throws LanguageNotSupportedByPal {
        //TODO: map for pal classes per language
        if (palClass == null) {
            LangAndPlatform lang = getLangAndPlatform(language);
            palClass = new PalClass(server, new IdeaFile(lang.getFileLocator()));
            palClass.update();
        }
        return palClass;
    }

    public PalFunctions createSnapshot(Language language) throws LanguageNotSupportedByPal {
        return new PalServerFunctionsAbsentLocally(new PalServerFunctions(server, getLangAndPlatform(language).getPalLanguage()), getPalClass(language));
    }

    public static Key<PalService> PAL_SERVICE = Key.create("pal.service");
    public static PalService getInstance(Project project) {
        return project.getUserData(PAL_SERVICE);
    }

    public void addServerListener(PalServerListener serverListener) {
        server.addListener(serverListener);
    }

    public void ping() {
        server.ping();
    }

    private LangAndPlatform getLangAndPlatform(Language language) throws LanguageNotSupportedByPal {
        LangAndPlatform lap = findLangAndPlatform(language);
        if (lap == null) throw new LanguageNotSupportedByPal();
        return lap;
    }

    private LangAndPlatform findLangAndPlatform(Language language)  {
        if (language == null) return null;
        for (LangAndPlatform lap: langsAndPlatforms) {
            if (lap.matches(language)) {
                return lap;
            }
        }
        return null;
    }

    public void registerLangAndPlatform(Class<? extends LangAndPlatform> lapClass) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        if (ProjectBasedLanguage.class.isAssignableFrom(lapClass)) {
            langsAndPlatforms.add(lapClass.getConstructor(Project.class).newInstance(project));
        }
        else {
            langsAndPlatforms.add(lapClass.newInstance());
        }
    }

    public static boolean isLanguageSupported(AnActionEvent event) {
        return PalService.getInstance(event.getProject()).isLanguageSupported(event.getData(LangDataKeys.LANGUAGE));
    }

    private boolean isLanguageSupported(@Nullable Language language) {
        return findLangAndPlatform(language) != null;
    }

    //TODO: probably it is better to have it in PalClass which already knows about language and all fucntions...
    public FunctionCallString getFunctionCallString(Language language, PalFunction function) {
        return getLangAndPlatform(language).getFunctionCallString(function.getId());
    }
}
