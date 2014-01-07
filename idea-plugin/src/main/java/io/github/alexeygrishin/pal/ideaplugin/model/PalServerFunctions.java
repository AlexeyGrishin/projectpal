package io.github.alexeygrishin.pal.ideaplugin.model;

import io.github.alexeygrishin.pal.api.PalFunction;
import io.github.alexeygrishin.pal.ideaplugin.remote.PalServer;
import io.github.alexeygrishin.tools.Listenable;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class PalServerFunctions extends Listenable<FunctionsContainerListener> implements PalFunctions {
    private List<PalFunction> knownFunctions = new LinkedList<PalFunction>();

    private PalServer server;
    private AtomicBoolean loading;
    private String language;

    public PalServerFunctions(PalServer server, String language) {
        this.language = language;
        initListeners(FunctionsContainerListener.class);
        this.server = server;
        this.loading = new AtomicBoolean(false);
    }

    private void reset(String language) {
        knownFunctions = server.getPopular(null);
    }

    @Override
    public Collection<PalFunction> update(@NotNull String newFilter) {
        listeners().onChangingStart(this);
        synchronized (this) {
            loading.set(true);
            if (newFilter.isEmpty()) {
                reset(language);
            }
            else {
                search(newFilter, language);
            }
            loading.set(false);
        }
        listeners().onChangingEnd(this);
        return knownFunctions;
    }

    private void search(String newFilter, String language) {
        knownFunctions = server.search(language, newFilter);
    }


    @Override
    public Collection<PalFunction> getFunctions() {
        return knownFunctions;
    }

    @Override
    public boolean canInclude(PalFunction function) {
        return false;
    }

    @Override
    public void setIncluded(PalFunction function, boolean include) {
        throw new IllegalStateException("Cannot edit function in server functions snapshot");
    }

    @Override
    protected void justAfterListenerAdded(FunctionsContainerListener listener) {
        if (!loading.get())
            listener.onChangingEnd(this);
    }
}
