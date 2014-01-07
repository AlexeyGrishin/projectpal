package io.github.alexeygrishin.pal.ideaplugin.model;

import io.github.alexeygrishin.pal.api.PalFunction;

import java.util.Collection;

public interface FunctionsContainer {
    Collection<PalFunction> getFunctions();
    void addListener(FunctionsContainerListener listener);
    void removeListener(FunctionsContainerListener listener);
    boolean canInclude(PalFunction function);
    void setIncluded(PalFunction function, boolean include);
}
