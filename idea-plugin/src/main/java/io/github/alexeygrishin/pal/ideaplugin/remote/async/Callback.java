package io.github.alexeygrishin.pal.ideaplugin.remote.async;

public interface Callback<T> {
    public void action(T result);
}
