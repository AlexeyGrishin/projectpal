package io.github.alexeygrishin.pal.ideaplugin.remote;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class ServerStateWrapper implements InvocationHandler {
    private Object wrapped;
    private PalServerListener listener;

    private boolean wasFailed = false;

    public ServerStateWrapper(Object wrapped, PalServerListener listener) {
        this.wrapped = wrapped;
        this.listener = listener;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        listener.onConnectionStart();
        try {
            Object result = method.invoke(wrapped, args);
            listener.onConnectionEnd();
            if (wasFailed) {
                wasFailed = false;
                listener.onConnectionRestore();
            }
            return result;
        }
        catch (InvocationTargetException e) {
            fail(e.getCause());
            throw e.getCause();
        }
        catch (Throwable e) {
            fail(e);
            throw e;
        }
    }

    private void fail(Throwable e) {
        wasFailed = true;
        listener.onConnectionFail(e.getMessage());
    }

    public static <T> T wrap(Object obj, Class<T> kls, PalServerListener listener) {
        return (T) Proxy.newProxyInstance(obj.getClass().getClassLoader(), new Class[] {kls}, new ServerStateWrapper(obj, listener));
    }
}
