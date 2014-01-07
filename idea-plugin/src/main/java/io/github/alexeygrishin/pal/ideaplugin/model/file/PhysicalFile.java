package io.github.alexeygrishin.pal.ideaplugin.model.file;

public interface PhysicalFile {

    public boolean exists();

    public void setContent(String content);

    public void insertBefore(String marker, String content);

    public String getContent();

    public String getLanguage();

    public String getClassNameToReference();
}
