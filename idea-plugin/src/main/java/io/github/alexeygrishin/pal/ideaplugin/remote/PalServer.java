package io.github.alexeygrishin.pal.ideaplugin.remote;

import io.github.alexeygrishin.pal.api.PalFunction;
import io.github.alexeygrishin.pal.ideaplugin.remote.api.PalApi;
import io.github.alexeygrishin.tools.Listenable;
import org.apache.cxf.jaxrs.client.JAXRSClientFactory;

import java.util.Collections;
import java.util.List;

public class PalServer extends Listenable<PalServerListener> {
    private static PalServer ourInstance = new PalServer();
    private final PalApi api;
    private final PalServerState state;

    public static PalServer getInstance() {
        return ourInstance;
    }


    private PalServer() {
        initListeners(PalServerListener.class);
        state = new PalServerState();
        addListener(state);
        api = ServerStateWrapper.wrap(JAXRSClientFactory.create("http://localhost:4567", PalApi.class,  Collections.singletonList(new GsonProvider<Object>())), PalApi.class, listeners());
    }

    public List<PalFunction> search(String language, String q) {
        return api.search(q, "slow").getValue();
    }

    public List<PalFunction> getPopular(String language) {
        return api.search("", "slow").getValue();
    }

    public String getFunction(String id, String language) {
        return api.getImplementation(id, language).getValue().getClassBody();
    }


    public void ping() {
        api.getLanguages();
    }

    @Override
    protected void justAfterListenerAdded(PalServerListener listener) {
        state.fireIfFailed(listener);
    }
}
