package io.github.alexeygrishin.pal.ideaplugin.remote.async;

import java.util.concurrent.Future;

public interface AsyncCallback {
    public <R> Future<R> on(R result);
    public <R> Future<R> on(R result, Callback<R> callback);
}
