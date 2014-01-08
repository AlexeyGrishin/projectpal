package io.github.alexeygrishin.tools.threads;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface BackgroundThread {
    public boolean read() default false;
}
