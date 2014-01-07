package io.github.alexeygrishin.tools.ui;

import com.intellij.util.ui.ColumnInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.lang.reflect.Modifier;

public abstract class SimpleColumn<A, B> extends ColumnInfo<A, B> {
    TableCell description;

    public SimpleColumn() {
        super("");
        description = getDescription(this);
        setName(description.name());
    }

    @NotNull
    private static TableCell getDescription(Object o) {
        TableCell annotation = o.getClass().getAnnotation(TableCell.class);
        if (annotation == null) throw new IllegalArgumentException("TODO");
        return annotation;
    }

    private static <T> T newInstanceOrNull(Class<T> kls) {
        if (!isEditable(kls)) return null;
        try {
            return kls.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static <T> boolean isEditable(Class<T> kls) {
        return !(kls == null || kls.isInterface() || ((kls.getModifiers() & Modifier.ABSTRACT) == Modifier.ABSTRACT));
    }

    @Nullable
    @Override
    public TableCellRenderer getRenderer(A a) {
        return newInstanceOrNull(description.renderWith());
    }

    @Nullable
    @Override
    public TableCellEditor getEditor(A o) {
        return newInstanceOrNull(description.editWith());
    }

    @Override
    public boolean isCellEditable(A a) {
        return isEditable(description.editWith());
    }

    @Override
    public int getWidth(JTable table) {
        return description.width();
    }
}
