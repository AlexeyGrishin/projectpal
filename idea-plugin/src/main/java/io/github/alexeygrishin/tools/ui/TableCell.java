package io.github.alexeygrishin.tools.ui;

import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface TableCell {
    public Class<? extends TableCellRenderer> renderWith() default TableCellRenderer.class;
    public Class<? extends TableCellEditor> editWith() default TableCellEditor.class;
    public String name();
    public int width() default -1;

}
