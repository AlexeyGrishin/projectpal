package io.github.alexeygrishin.tools;

public interface ListenerContainer<T> {
    void addListener(T listener);
    void removeListener(T listener);
    T getListener();

}
