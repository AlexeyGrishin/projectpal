package io.github.alexeygrishin.pal.api;

import java.util.List;

public class PalFunction implements FunctionId {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FunctionId that = (FunctionId) o;

        if (!id.equals(that.getId())) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public static String toString(Object o) {
        if (o instanceof PalFunction) {
            PalFunction function = (PalFunction) o;
            return function.getId();
        }
        return null;
    }
}
