package io.github.alexeygrishin.pal.server;

public class JsonResult {
    private boolean ok;
    private String error;
    private Object value;

    public JsonResult(boolean ok, String error, Object value) {
        this.ok = ok;
        this.error = error;
        this.value = value;
    }

    public static JsonResult ok(Object value) {
        return new JsonResult(true, null, value);
    }

    public static JsonResult error(Exception e) {
        return new JsonResult(false, e.getMessage(), null);
    }

    public boolean isOk() {
        return ok;
    }

    public String getError() {
        return error;
    }

    public Object getValue() {
        return value;
    }
}
