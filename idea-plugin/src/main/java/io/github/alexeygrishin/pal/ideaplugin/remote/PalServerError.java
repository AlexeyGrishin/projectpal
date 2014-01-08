package io.github.alexeygrishin.pal.ideaplugin.remote;

public class PalServerError extends RuntimeException {
    public PalServerError() {
    }

    public PalServerError(String message) {
        super(message);
    }

    public PalServerError(String message, Throwable cause) {
        super(message, cause);
    }

    public PalServerError(Throwable cause) {
        super(cause);
    }

    public PalServerError(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
