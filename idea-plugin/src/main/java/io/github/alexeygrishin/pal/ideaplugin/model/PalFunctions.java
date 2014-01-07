package io.github.alexeygrishin.pal.ideaplugin.model;

import io.github.alexeygrishin.pal.api.PalFunction;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public interface PalFunctions extends FunctionsContainer {
    Collection<PalFunction> update(@NotNull String newFilter);

    @Override
    Collection<PalFunction> getFunctions();
}
