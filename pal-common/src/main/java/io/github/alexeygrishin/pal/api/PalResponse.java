package io.github.alexeygrishin.pal.api;

import java.util.List;

public class PalResponse {

    private List<PalFunction> functions;
    private String classBody;

    public PalResponse(List<PalFunction> functions, String classBody) {
        this.functions = functions;
        this.classBody = classBody;
    }

    public List<PalFunction> getFunctions() {
        return functions;
    }

    public String getClassBody() {
        return classBody;
    }
}
