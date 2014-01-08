package io.github.alexeygrishin.pal.ideaplugin.model.file;

import org.jetbrains.annotations.NotNull;

/**
 * Represents physical file for Pal class/module
 */
public interface PhysicalFile {

    /**
     *
     * @return true if file physicall exists
     */
    public boolean exists();

    /**
     * Replaces file content with provided one. Creates the file if not created before
     * @param content any content
     */
    public void setContent(@NotNull String content);

    /**
     * Inserts provided content before the line with marker inside. Creates the file if not created before
     * @param marker string to find in the current file content
     * @param content text to insert before the marker line
     */
    public void insertBefore(@NotNull String marker, @NotNull String content);

    /**
     *
     * @return file content. Creates the file if not create before
     * WARNING: if you call this method when file does not exist it will be created.
     */
    public @NotNull String getContent();

    /**
     *
     * @return path to file, for showing to user
     */
    public @NotNull String getPathAsString();
}
