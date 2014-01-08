package io.github.alexeygrishin.pal.ideaplugin.remote;


class PalServerState implements PalServerListener {
    private boolean failed = false;
    private String reason = "";
    private final Object lock = new Object();

    @Override
    public void onConnectionStart() {

    }

    @Override
    public void onConnectionEnd() {

    }

    @Override
    public void onConnectionFail(String error) {
        synchronized (lock) {
            failed = true;
            reason = error;
        }
    }

    @Override
    public void onConnectionRestore() {
        synchronized (lock) {
            failed = false;
        }
    }

    public void fireIfFailed(PalServerListener listener) {
        boolean wasFailed = false;
        String wasReason = "";
        synchronized (lock) {
            wasFailed = failed;
            wasReason = reason;
        }
        if (wasFailed) {
            listener.onConnectionFail(wasReason);
        }
    }

    public boolean isAvailable() {
        return !failed;
    }
}
