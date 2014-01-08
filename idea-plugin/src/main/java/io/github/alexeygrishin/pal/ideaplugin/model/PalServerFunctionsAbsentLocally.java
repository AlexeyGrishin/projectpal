package io.github.alexeygrishin.pal.ideaplugin.model;

import io.github.alexeygrishin.pal.api.PalFunction;
import io.github.alexeygrishin.tools.Listenable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Wrapper that excludes functions from pal class from the original collection
 */
class PalServerFunctionsAbsentLocally implements PalFunctions  {

    private PalFunctions serverFunctions;
    private PalClass palClass;
    private List<PalFunction> functions = new ArrayList<PalFunction>();

    public PalServerFunctionsAbsentLocally(PalServerFunctions serverFunctions, PalClass palClass) {
        this.serverFunctions = serverFunctions;
        this.palClass = palClass;
    }

    @Override
    public Collection<PalFunction> update(@NotNull String newFilter) {
        functions = excludePalClass(serverFunctions.update(newFilter));
        return functions;
    }

    private List<PalFunction> excludePalClass(Collection<PalFunction> allFunctions) {
        functions = new ArrayList<PalFunction>(allFunctions.size());
        for (PalFunction function: allFunctions) {
            if (!palClass.contains(function)) {
                functions.add(function);
            }
        }
        return functions;
    }

    @Override
    public Collection<PalFunction> getFunctions() {
        return functions;
    }

}
