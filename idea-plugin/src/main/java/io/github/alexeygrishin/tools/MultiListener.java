package io.github.alexeygrishin.tools;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class MultiListener<T> implements InvocationHandler{

    private final List<T> listeners = Collections.synchronizedList(new LinkedList<T>());


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //TODO: exceptions handling
        List<T> listenersToCall = new ArrayList<T>(listeners.size());
        synchronized (listeners) {
            listenersToCall.addAll(listeners);
        }
        for (T listener: listenersToCall) {
            method.invoke(listener, args);
        }
        return null;
    }

    public class MultiListenerContainer implements ListenerContainer<T> {

        private T listener;

        public MultiListenerContainer(T listener) {
            this.listener = listener;
        }

        @Override
        public void addListener(T listener) {
            listeners.add(listener);
        }

        @Override
        public void removeListener(T listener) {
            listeners.remove(listener);
        }

        @Override
        public T getListener() {
            return listener;
        }
    }

    public MultiListenerContainer createContainer(T proxy) {
        return new MultiListenerContainer(proxy);
    }

    public static <T>  ListenerContainer<T> wrap(Class<T> kls) {
        MultiListener<T> handler = new MultiListener<T>();
        T proxy = (T) Proxy.newProxyInstance(kls.getClassLoader(), new Class[]{kls, ListenerContainer.class}, handler);
        return handler.createContainer(proxy);
    }
}
