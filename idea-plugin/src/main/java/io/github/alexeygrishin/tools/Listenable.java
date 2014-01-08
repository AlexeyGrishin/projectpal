package io.github.alexeygrishin.tools;

import io.github.alexeygrishin.pal.ideaplugin.model.PalClassListener;

import java.util.List;

/**
 * Base class like "Observable" for generic-based listeners.
 * Subclasses:
 *  - shall call {@link #initListeners(Class)} in constructor
 *  - may call {@link #listeners()} to get access to all listeners via single instance
 * @param <T>
 */
public class Listenable<T> {

    private ListenerContainer<T> container = null;
    private T listeners = null;

    public final void addListener(T listener) {
        checkInitialized();
        container.addListener(listener);
        justAfterListenerAdded(listener);
    }

    protected void justAfterListenerAdded(T listener) {
        //do nothing, but could be overriden in subclasses
    }

    public final void removeListener(T listener) {
        checkInitialized();
        container.removeListener(listener);
    }

    private void checkInitialized() {
        if (container == null || listeners == null) {
            throw new ListenersListWasNotInitialized("Cannot access listeners list - it was not initialized. Please call #initListeners in your constructor");
        }
    }

    protected final T initListeners(Class<T> kls) {
        container = MultiListener.wrap(kls);
        listeners = container.getListener();
        return listeners;
    }


    protected final T listeners() {
        checkInitialized();
        return listeners;
    }

    public void addListeners(Iterable<T> listeners) {
        checkInitialized();
        for (T listener: listeners)
            addListener(listener);
    }
}
