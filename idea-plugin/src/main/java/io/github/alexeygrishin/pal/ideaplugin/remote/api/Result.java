package io.github.alexeygrishin.pal.ideaplugin.remote.api;

public class Result<T> {
    private boolean ok;
    private String error;
    private T value;

    public boolean isOk() {
        return ok;
    }

    public void setOk(boolean ok) {
        this.ok = ok;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }
}
