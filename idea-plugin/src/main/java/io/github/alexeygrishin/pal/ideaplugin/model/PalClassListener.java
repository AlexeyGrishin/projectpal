package io.github.alexeygrishin.pal.ideaplugin.model;

public interface PalClassListener {

    void onPalClassCreation(PalClass source);

    void onPalClassChange(PalClass source);
}
