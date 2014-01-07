package io.github.alexeygrishin.pal.ideaplugin.model;

import io.github.alexeygrishin.pal.api.PalFunction;
import io.github.alexeygrishin.pal.ideaplugin.model.file.PhysicalFile;
import io.github.alexeygrishin.pal.ideaplugin.remote.PalServer;
import io.github.alexeygrishin.pal.tools.Module;
import io.github.alexeygrishin.tools.Listenable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class PalClass extends Listenable<FunctionsContainerListener> implements FunctionsContainer {

    private Set<PalFunction> functions = new HashSet<PalFunction>();
    private Set<String> builtin = new HashSet<String>();
    private PalServer server;
    private PhysicalFile realFile;
    private AtomicBoolean loaded = new AtomicBoolean(false);

    public PalClass(PalServer server, PhysicalFile realFile) {
        //TODO: shall watch real file and update self if user changed it
        initListeners(FunctionsContainerListener.class);
        this.server = server;
        this.realFile = realFile;
    }

    @Override
    protected void justAfterListenerAdded(FunctionsContainerListener listener) {
        if (loaded.get())
            listener.onChangingEnd(this);
    }

    private void loadFunctions() {
        Module module = new Module(realFile.getContent());
        for (String fId: module.getIdsForType("function")) {
            functions.add(new PalFunction(fId, null, Collections.<String>emptyList()));
        }
        for (String bId: module.getIdsForType("builtin")) {
            builtin.add(bId);
        }
    }

    @Override
    public Collection<PalFunction> getFunctions() {
        return functions;
    }

    public void addFunction(PalFunction function) {
        listeners().onChangingStart(this);
        Module body = new Module(server.getFunction(function.getId(), realFile.getLanguage()));
        if (!realFile.exists()) {
            realFile.setContent(body.toString());
        }
        else {
            for (String fId: body.getIdsForType("function")) {
                PalFunction toInsert = new PalFunction(fId, null, null);
                if (!functions.contains(toInsert)) {
                    realFile.insertBefore(":addbefore", body.getSection("function", fId));
                }
            }
            for (String bId: body.getIdsForType("builtin")) {
                if (!builtin.contains(bId)) {
                    realFile.insertBefore(":addbefore", body.getSection("builtin", bId));
                }
            }

        }
        loadFunctions();    //not optimal, just quick solution
        listeners().onChangingEnd(this);
    }

    public void removeFunction(PalFunction function) {
        listeners().onChangingStart(this);
        functions.remove(function);
        //TODO: remove from real file as well
        listeners().onChangingEnd(this);
    }

    public void toggleFunction(PalFunction function) {
        if (functions.contains(function))
            removeFunction(function);
        else
            addFunction(function);
    }

    public void update() {
        if (!loaded.get() && realFile.exists()) {
            listeners().onChangingStart(this);
            loadFunctions();
            loaded.set(true);
            listeners().onChangingEnd(this);
        }
    }

    @Override
    public boolean canInclude(PalFunction function) {
        return !functions.contains(function);
    }

    @Override
    public void setIncluded(PalFunction function, boolean include) {
        if (include && !contains(function)) {
            addFunction(function);
        }
    }

    public String getClassName() {
        return realFile.getClassNameToReference();
    }

    public boolean contains(PalFunction function) {
        return functions.contains(function);
    }
}
