package io.github.alexeygrishin.tools.threads;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public interface Executor {
    <R>Future<R> execute(Callable<R> action);
}
