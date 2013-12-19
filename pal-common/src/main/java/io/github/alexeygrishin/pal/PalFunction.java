package io.github.alexeygrishin.pal;

import java.util.List;

public class PalFunction {
    private String id;
    private String description;
    private List<String> tags;

    public PalFunction(String id, String description, List<String> tags) {
        this.id = id;
        this.description = description;
        this.tags = tags;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

}
