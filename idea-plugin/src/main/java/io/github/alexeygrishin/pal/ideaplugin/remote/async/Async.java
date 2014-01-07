package io.github.alexeygrishin.pal.ideaplugin.remote.async;

import io.github.alexeygrishin.tools.threads.Executor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public class Async implements InvocationHandler, AsyncInterface {

    private final Object wrapped;
    private Executor invoker;
    private ThreadLocal<Boolean> asyncMode = new ThreadLocal<Boolean>();
    private ThreadLocal<PostponedCall> postponedCall = new ThreadLocal<PostponedCall>();
    
    class PostponedCall {
        public final Method method;
        public final Object[] args;

        PostponedCall(Method method, Object[] args) {
            this.method = method;
            this.args = args;
        }

        public <R> R apply() throws InvocationTargetException, IllegalAccessException {
            return (R) method.invoke(wrapped, args);
        }
    }

    public Async(Object wrapped, Executor invoker) {
        this.wrapped = wrapped;
        this.invoker = invoker;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getDeclaringClass() == AsyncInterface.class) {
            return method.invoke(this, args);
        }
        else if (asyncMode.get() == Boolean.TRUE) {
            postponedCall.set(new PostponedCall(method, args));
            asyncMode.set(false);
        }
        else {
            return method.invoke(wrapped, args);
        }
        return null;
    }

    @Override
    public AsyncCallback async() {
        asyncMode.set(true);
        postponedCall.set(null);
        return new AsyncCallback() {
            @Override
            public <R> Future<R> on(R result) {
                return on(result, new Callback<R>() {
                    @Override
                    public void action(R result) {}
                });
            }

            private void checkPostponedCall() {
                if (postponedCall.get() == null) throw new IllegalStateException("You shall to call one of this class methods");
            }

            @Override
            public <R> Future<R> on(R result, final Callback<R> callback) {
                checkPostponedCall();
                return invoker.execute(new Callable<R>() {
                    @Override
                    public R call() throws Exception {
                        R result = postponedCall.get().apply();
                        callback.action(result);
                        return result;
                    }
                });
            }
        };
    }

    public static <T extends AsyncInterface> T wrap(Object api, Class<T> kls, Executor invoker) {
        return (T) Proxy.newProxyInstance(api.getClass().getClassLoader(), new Class[] {kls}, new Async(api, invoker));
    }

}
