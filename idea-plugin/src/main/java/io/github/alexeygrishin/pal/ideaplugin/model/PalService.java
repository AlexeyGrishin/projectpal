package io.github.alexeygrishin.pal.ideaplugin.model;

import com.intellij.lang.Language;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import io.github.alexeygrishin.pal.api.PalFunction;
import io.github.alexeygrishin.pal.ideaplugin.model.file.IdeaFile;
import io.github.alexeygrishin.pal.ideaplugin.model.lang.FunctionCallString;
import io.github.alexeygrishin.pal.ideaplugin.model.lang.LangAndPlatformEx;
import io.github.alexeygrishin.pal.ideaplugin.model.lang.ProjectBasedLanguage;
import io.github.alexeygrishin.pal.ideaplugin.remote.PalServer;
import io.github.alexeygrishin.pal.ideaplugin.remote.PalServerListener;
import io.github.alexeygrishin.pal.ideaplugin.ui.Notifier;
import io.github.alexeygrishin.tools.Listenable;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;

/**
 * Facade for most of required functionality to be used by UI components.
 * Could be obtained from project, like this:
 * <code>
 *     PalService.getInstance(project);
 * </code>
 *
 * Also there are several static methods that could be used from any context
 */
public class PalService extends Listenable<PalServiceListener> {
    private PalClass palClass;
    private PalServer server;
    private List<LangAndPlatformEx> langsAndPlatforms = new LinkedList<LangAndPlatformEx>();
    private static List<PalClassListener> listenersForPalClasses = new LinkedList<PalClassListener>();
    private Project project;

    public PalService(Project project) {
        this.project = project;
        initListeners(PalServiceListener.class);
        server = PalServer.getInstance();
    }

    public static Key<PalService> PAL_SERVICE = Key.create("pal.service");

    public static PalService getInstance(Project project) {
        return project.getUserData(PAL_SERVICE);
    }


    /**
     * Helper method in order to enable/disable actions
     * @param event action event
     * @return true if language related to this event is supported by pal
     */
    public static boolean isLanguageSupported(AnActionEvent event) {
        return PalService.getInstance(event.getProject()).isLanguageSupported(event.getData(LangDataKeys.LANGUAGE));
    }

    /**
     *
     * @return true if server available (warn: it just returns latest known state and does not perform any real call)
     */
    public static boolean isServerAvailable() {
        return PalServer.getInstance().isAvailable();
    }

    public static void addServerListener(PalServerListener serverListener) {
        PalServer.getInstance().addListener(serverListener);
    }

    /**
     * Returns pal class related to the provided language.
     * TODO: right now returns only one instance
     * @param language
     * @return pal class
     * @throws LanguageNotSupportedByPal if language is null or not supported
     */
    public PalClass getPalClass(@Nullable Language language) throws LanguageNotSupportedByPal {
        //TODO: map for pal classes per language
        if (palClass == null) {
            LangAndPlatformEx lang = getLangAndPlatform(language);
            palClass = new PalClass(server, new IdeaFile(lang.getFileLocator()), lang);
            palClass.addListeners(listenersForPalClasses);
            palClass.update();
        }
        return palClass;
    }

    /**
     * Returns live functions list which could be updated/filter from server
     * @param language
     * @return
     * @throws LanguageNotSupportedByPal
     */
    public PalFunctions createSnapshot(Language language) throws LanguageNotSupportedByPal {
        return new PalServerFunctions(server, getLangAndPlatform(language).getPalLanguage());
    }

    //for internal usage
    public void registerLangAndPlatform(Class<? extends LangAndPlatformEx> lapClass) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        if (ProjectBasedLanguage.class.isAssignableFrom(lapClass)) {
            langsAndPlatforms.add(lapClass.getConstructor(Project.class).newInstance(project));
        }
        else {
            langsAndPlatforms.add(lapClass.newInstance());
        }
    }


    private LangAndPlatformEx getLangAndPlatform(Language language) throws LanguageNotSupportedByPal {
        LangAndPlatformEx lap = findLangAndPlatform(language);
        if (lap == null) throw new LanguageNotSupportedByPal();
        return lap;
    }

    private LangAndPlatformEx findLangAndPlatform(Language language)  {
        if (language == null) return null;
        for (LangAndPlatformEx lap: langsAndPlatforms) {
            if (lap.matches(language)) {
                return lap;
            }
        }
        return null;
    }


    private boolean isLanguageSupported(@Nullable Language language) {
        return findLangAndPlatform(language) != null;
    }

    //for internal usage
    public static void addPalClassesListener(PalClassListener palClassListener) {
        listenersForPalClasses.add(palClassListener);
    }
}
