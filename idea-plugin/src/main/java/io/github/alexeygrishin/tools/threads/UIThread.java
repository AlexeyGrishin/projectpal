package io.github.alexeygrishin.tools.threads;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface UIThread {
    public boolean read() default false;
    public boolean write() default false;
}
