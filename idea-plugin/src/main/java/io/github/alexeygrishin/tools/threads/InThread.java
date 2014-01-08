package io.github.alexeygrishin.tools.threads;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.util.Computable;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

/**
 * Helper to run actions in valid threads :)
 * It is possible to wrap classes (like listeners or just Runnables) so their methods will be exected in the thread
 * according to annotations
 *
 * Examples:
 * <code>
 * InThread.execute(new Runnable() {
 *
 *    @UIThread(write = true)
 *    public void run() {
 *        //this code will be asynchronously executed in UI thread inside write action
 *    }
 *
 * });
 *
 * InThread.execute(new Runnable() {
 *
 *    @BackgroundThread(read = true)
 *    @Sync
 *    public void run() {
 *        //this code will be synchronously executed in background thread inside read action
 *    }
 *
 * });
 * </code>
 *
 *
 */
public class InThread {


    public static <T> T inUIThread(T object, Class<T> cls) {
        return (T) Proxy.newProxyInstance(object.getClass().getClassLoader(), new Class[] {cls}, new ThreadInvocationHandler(object, ThreadInvocationHandler.UIThreadAsync));
    }

    public static <T> T inConfiguredThread(T object, Class<? super T> cls) {
        return (T) Proxy.newProxyInstance(object.getClass().getClassLoader(), new Class[] {cls}, new ThreadInvocationHandler(object));
    }

    public static void execute(Runnable runnable) {
        inConfiguredThread(runnable, Runnable.class).run();
    }

    public static <T> T executeAndReturn(Computable<T> action) {
        return inConfiguredThread(action, Computable.class).compute();
    }



}
