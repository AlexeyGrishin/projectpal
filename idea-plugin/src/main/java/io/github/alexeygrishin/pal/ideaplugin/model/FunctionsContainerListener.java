package io.github.alexeygrishin.pal.ideaplugin.model;

public interface FunctionsContainerListener {
    public void onChangingStart(FunctionsContainer source);

    public void onChangingEnd(FunctionsContainer source);
}
