package io.github.alexeygrishin.pal.ideaplugin.model;

import io.github.alexeygrishin.pal.api.PalFunction;
import io.github.alexeygrishin.pal.ideaplugin.remote.PalServer;
import io.github.alexeygrishin.tools.Listenable;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * List of pal functions from pal server
 */
public class PalServerFunctions implements PalFunctions {
    private List<PalFunction> knownFunctions = new LinkedList<PalFunction>();

    private PalServer server;
    private AtomicBoolean loading;
    private String language;

    public PalServerFunctions(PalServer server, String language) {
        this.language = language;
        this.server = server;
        this.loading = new AtomicBoolean(false);
    }

    private void reset(String language) {
        knownFunctions = server.getPopular(null);
    }

    @Override
    public Collection<PalFunction> update(@NotNull String newFilter) {
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
        return knownFunctions;
    }

    private void search(String newFilter, String language) {
        knownFunctions = server.search(language, newFilter);
    }


    @Override
    public Collection<PalFunction> getFunctions() {
        return knownFunctions;
    }

}
