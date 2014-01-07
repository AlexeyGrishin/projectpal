package io.github.alexeygrishin.pal.ideaplugin.remote;

public interface PalServerListener {
    /**
     * Called when call to server initiated
     */
    void onConnectionStart();

    /**
     * Called when call to server finished (successfully or not).
     */
    void onConnectionEnd();

    /**
     * Called when call to server failed
     * @param error error message
     */
    void onConnectionFail(String error);

    /**
     * Called on first success call after failed connection.
     */
    void onConnectionRestore();
}
