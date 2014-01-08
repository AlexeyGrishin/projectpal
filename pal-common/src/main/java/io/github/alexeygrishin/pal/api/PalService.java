package io.github.alexeygrishin.pal.api;

import java.util.List;

/**
 * Java-facade for pal functionality
 */
public interface PalService {

    public List<PalFunction> lookup(String q, int from, int limit);

    public PalResponse getFunction(String language, String id);

    public List<String> getSupportedLanguages();

}
