package io.github.alexeygrishin.tools.threads;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.util.Computable;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

class ThreadInvocationHandler implements InvocationHandler {
    private Object wrapped;
    private Invoker defaultInvoker;

    ThreadInvocationHandler(Object wrapped) {
        this(wrapped, new InvokeAsIs());
    }

    ThreadInvocationHandler(Object wrapped, Invoker defaultInvoker) {

        this.wrapped = wrapped;
        this.defaultInvoker = defaultInvoker;
    }


    @Override
    public Object invoke(Object proxy, final Method method, final Object[] args) throws Throwable {

        return getInvoker(method).invokeInValidThread(new Computable<Object>() {
            @Override
            public Object compute() {
                try {
                    return method.invoke(wrapped, args);
                } catch (IllegalAccessException e) {
                    //impossible
                    e.printStackTrace();
                    return null;
                } catch (InvocationTargetException e) {
                    throw new RuntimeException(e.getCause());
                }
            }
        });
    }

    private Invoker getInvoker(Method method) {
        boolean requireSync = method.getReturnType() != Void.class || method.isAnnotationPresent(Sync.class);
        boolean read = false, write = false;
        Invoker invoker = defaultInvoker;
        if (isAnnotationPresent(method, UIThread.class)) {
            UIThread annotation = getAnnotation(method, UIThread.class);
            read = annotation.read();
            write = annotation.write();
            invoker = requireSync ? UIThreadSync : UIThreadAsync;
        }
        else if (isAnnotationPresent(method, BackgroundThread.class))  {
            BackgroundThread annotation = getAnnotation(method, BackgroundThread.class);
            read = annotation.read();
            invoker = requireSync ? BackgroundSync : BackgroundAsync;
        }
        else if (isAnnotationPresent(method, AnyThread.class)) {
            read = getAnnotation(method, AnyThread.class).read();
        }
        return readWrite(invoker, requireSync, read, write);
    }

    private Invoker readWrite(Invoker base, boolean sync, boolean read, boolean write) {
        if (write) {
            return new NestedInvoker(base, sync ? WriteSync : WriteAsync);
        }
        if (read) {
            return new NestedInvoker(base, sync ? ReadSync : ReadAsync);
        }
        return base;
    }

    private boolean isAnnotationPresent(Method method, Class<? extends Annotation> annotationClass) {
        return getAnnotation(method, annotationClass) != null;
    }

    private <T extends Annotation> T getAnnotation(Method method, Class<T> annotationClass) {
        Method realMethod;
        try {
            realMethod = wrapped.getClass().getMethod(method.getName(), method.getParameterTypes());
        } catch (NoSuchMethodException e) {
            //impossible
            e.printStackTrace();
            return null;
        }
        Annotation[] annotations = realMethod.getDeclaredAnnotations();
        if (annotations.length > 0) {
            for (Annotation annotation: annotations) {
                if (annotation.annotationType().equals(annotationClass)) return annotationClass.cast(annotation);
            }
            return null;
        }
        else {
            return realMethod.getAnnotation(annotationClass);
        }
    }

    static abstract class Invoker {
        abstract <T> T invoke(Computable<T> action);

        boolean inMyThread() {
            return false;
        }

        <T> T invokeInValidThread(Computable<T> action) {
            if (inMyThread()) {
                return action.compute();
            }
            else {
                return invoke(action);
            }
        }

    }

    static class InvokeAsIs extends Invoker {
        @Override
        public <T> T invoke(Computable<T> action) {
            return action.compute();
        }

        @Override
        boolean inMyThread() {
            return true;
        }
    }

    abstract static class InvokerWithoutResult extends Invoker {
        @Override
        public <T> T invoke(final Computable<T> action) {
            invoke(new Runnable() {
                @Override
                public void run() {
                    action.compute();
                }
            });
            return null;
        }
        abstract void invoke(Runnable action);
    }


    static final Invoker UIThreadAsync = new InvokerWithoutResult() {
        @Override
        public void invoke(Runnable action) {
            ApplicationManager.getApplication().invokeLater(action);
        }

        @Override
        boolean inMyThread() {
            return ApplicationManager.getApplication().isDispatchThread();
        }
    };

    static final Invoker UIThreadSync = new InvokerWithoutResult() {
        @Override
        public void invoke(Runnable action) {
            ApplicationManager.getApplication().invokeAndWait(action, ModalityState.defaultModalityState());
        }

        @Override
        boolean inMyThread() {
            return ApplicationManager.getApplication().isDispatchThread();
        }
    };

    static final Invoker ReadAsync = new InvokerWithoutResult() {
        @Override
        void invoke(Runnable action) {
            ApplicationManager.getApplication().runReadAction(action);
        }
    };

    static final Invoker ReadSync = new Invoker() {
        @Override
        public <T> T invoke(Computable<T> action) {
            return ApplicationManager.getApplication().runReadAction(action);
        }
    };

    static final Invoker WriteAsync = new InvokerWithoutResult() {
        @Override
        void invoke(Runnable action) {
            ApplicationManager.getApplication().runWriteAction(action);
        }
    };

    static final Invoker WriteSync = new Invoker() {
        @Override
        <T> T invoke(Computable<T> action) {
            return ApplicationManager.getApplication().runWriteAction(action);
        }
    };

    static class NestedInvoker extends Invoker {
        private final Invoker out;
        private final Invoker in;

        NestedInvoker(Invoker out, Invoker in) {

            this.out = out;
            this.in = in;
        }

        @Override
        <T> T invoke(final Computable<T> action) {
            return out.invokeInValidThread(new Computable<T>() {
                @Override
                public T compute() {
                    return in.invokeInValidThread(action);
                }
            });
        }
    }

    static final Invoker BackgroundSync = new Invoker() {
        @Override
        public <T> T invoke(Computable<T> action) {
            try {
                return ApplicationManager.getApplication().executeOnPooledThread(toCallable(action)).get();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return null;//not reachable
            } catch (ExecutionException e) {
                //impossible
                e.printStackTrace();
                return null;
            }
        }
    };

    static final Invoker BackgroundAsync = new InvokerWithoutResult() {
        @Override
        void invoke(Runnable action) {
            ApplicationManager.getApplication().executeOnPooledThread(action);
        }
    };

    private static <T> Callable<T> toCallable(final Computable<T> action) {
        return new Callable<T>() {
            @Override
            public T call() throws Exception {
                return action.compute();
            }
        };
    }
}
