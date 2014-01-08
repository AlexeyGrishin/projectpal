package io.github.alexeygrishin.pal.ideaplugin.model;

import io.github.alexeygrishin.pal.api.PalFunction;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * Pal functions collection that could be filtered
 */
public interface PalFunctions {
    /**
     * Performs filtering of the collection
     * @param newFilter filter to apply to the collection
     * @return new collection
     */
    Collection<PalFunction> update(@NotNull String newFilter);

    /**
     *
     * @return current collection state
     */
    Collection<PalFunction> getFunctions();
}
