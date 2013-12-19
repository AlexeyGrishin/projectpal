package io.github.alexeygrishin.pal;

import java.util.List;

public interface PalService {

    public List<PalFunction> lookup(String q, int from, int limit);

    public PalResponse getFunction(String language, String id);

    public List<String> getSupportedLanguages();

}
