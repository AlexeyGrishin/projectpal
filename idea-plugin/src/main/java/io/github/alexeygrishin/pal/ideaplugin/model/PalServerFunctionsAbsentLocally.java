package io.github.alexeygrishin.pal.ideaplugin.model;

import io.github.alexeygrishin.pal.api.PalFunction;
import io.github.alexeygrishin.tools.Listenable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class PalServerFunctionsAbsentLocally extends Listenable<FunctionsContainerListener> implements PalFunctions  {

    private PalFunctions serverFunctions;
    private PalClass palClass;
    private List<PalFunction> functions = new ArrayList<PalFunction>();

    public PalServerFunctionsAbsentLocally(PalServerFunctions serverFunctions, PalClass palClass) {
        initListeners(FunctionsContainerListener.class);
        this.serverFunctions = serverFunctions;
        this.palClass = palClass;
    }

    @Override
    public Collection<PalFunction> update(@NotNull String newFilter) {
        listeners().onChangingStart(this);
        functions = excludePalClass(serverFunctions.update(newFilter));
        listeners().onChangingEnd(this);
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

    @Override
    public boolean canInclude(PalFunction function) {
        return serverFunctions.canInclude(function);
    }

    @Override
    public void setIncluded(PalFunction function, boolean include) {
        serverFunctions.setIncluded(function, include);
    }
}
