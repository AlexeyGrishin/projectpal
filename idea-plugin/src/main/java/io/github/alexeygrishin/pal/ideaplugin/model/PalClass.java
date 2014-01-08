package io.github.alexeygrishin.pal.ideaplugin.model;

import io.github.alexeygrishin.pal.api.FunctionId;
import io.github.alexeygrishin.pal.api.PalFunction;
import io.github.alexeygrishin.pal.ideaplugin.model.file.PhysicalFile;
import io.github.alexeygrishin.pal.ideaplugin.model.lang.FunctionCallString;
import io.github.alexeygrishin.pal.ideaplugin.model.lang.LangAndPlatform;
import io.github.alexeygrishin.pal.ideaplugin.remote.PalServer;
import io.github.alexeygrishin.pal.tools.Module;
import io.github.alexeygrishin.tools.Listenable;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Represents Pal class in user's project.
 * Theoretically there could be several Pal classes in same project (for example one for java, secon one for client js).
 *
 * Operations made on this class immediately reflected on the physical file on disk.
 //TODO: shall watch real file and update self if user changed it
 */
public class PalClass extends Listenable<PalClassListener>  {

    public static final String ADDBEFORE = ":addbefore";
    public static final String TYPE_FUNCTION = "function";
    public static final String TYPE_BUILTIN = "builtin";
    private Set<? super FunctionId> functions = new HashSet<FunctionId>();
    private Set<String> builtin = new HashSet<String>();
    private PalServer server;
    private PhysicalFile realFile;
    private AtomicBoolean loaded = new AtomicBoolean(false);
    private LangAndPlatform lang;

    public PalClass(PalServer server, PhysicalFile realFile, LangAndPlatform lang) {
        initListeners(PalClassListener.class);
        this.server = server;
        this.realFile = realFile;
        this.lang = lang;
    }

    private void loadFunctions() {
        Module module = new Module(realFile.getContent());
        for (String fId: module.getIdsForType(TYPE_FUNCTION)) {
            functions.add(new PalFunction(fId, null, Collections.<String>emptyList()));
        }
        for (String bId: module.getIdsForType(TYPE_BUILTIN)) {
            builtin.add(bId);
        }
    }

    /**
     *
     * @return list of functions actually included into pal class
     */
    public Collection<? super FunctionId> getFunctions() {
        return functions;
    }

    /**
     * Adds provided function (if not added yet) to the pal class (and created class itself if not created before)
     * @param function function to be added
     * @return function call string (to insert into user's document if needed)
     */
    public FunctionCallString addFunction(@NotNull FunctionId function) {
        if (!contains(function)) {
            Module body = new Module(server.getFunction(function.getId(), lang.getPalLanguage()));
            if (!realFile.exists()) {
                realFile.setContent(body.toString());
                listeners().onPalClassCreation(this);
            }
            else {
                for (String fId: body.getIdsForType(TYPE_FUNCTION)) {
                    PalFunction toInsert = new PalFunction(fId, null, null);
                    if (!functions.contains(toInsert)) {
                        realFile.insertBefore(ADDBEFORE, body.getSection(TYPE_FUNCTION, fId));
                    }
                }
                for (String bId: body.getIdsForType(TYPE_BUILTIN)) {
                    if (!builtin.contains(bId)) {
                        realFile.insertBefore(ADDBEFORE, body.getSection(TYPE_BUILTIN, bId));
                    }
                }
            }
            loadFunctions();    //not optimal, just quick solution
            listeners().onPalClassChange(this);
        }
        return lang.getFunctionCallString(function.getId());
    }

    public void removeFunction(FunctionId function) {
        functions.remove(function);
        //TODO: remove from real file as well
        listeners().onPalClassChange(this);
    }

    public void update() {
        if (!loaded.get() && realFile.exists()) {
            loadFunctions();
            loaded.set(true);
            listeners().onPalClassChange(this);
        }
    }


    public boolean contains(@NotNull FunctionId function) {
        return functions.contains(function);
    }

    //use it for user notifications only
    public String getPath() {
        return realFile.getPathAsString();
    }
}
