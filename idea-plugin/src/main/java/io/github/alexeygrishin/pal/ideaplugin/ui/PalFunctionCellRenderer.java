package io.github.alexeygrishin.pal.ideaplugin.ui;

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.ColoredListCellRenderer;
import com.intellij.ui.JBColor;
import com.intellij.ui.SimpleColoredComponent;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.util.ui.GraphicsUtil;
import com.intellij.util.ui.UIUtil;
import io.github.alexeygrishin.pal.api.PalFunction;

import javax.swing.*;
import java.awt.*;

public class PalFunctionCellRenderer extends ColoredListCellRenderer {


    public PalFunctionCellRenderer() {
        setFont(UIUtil.getLabelFont());
    }

    @Override
    protected void customizeCellRenderer(JList list, Object value, int index, boolean selected, boolean hasFocus) {
        clear();
        if (value instanceof PalFunction) {
            renderPalFunction((PalFunction)value, selected, hasFocus);
        }
        else {
            append(value.toString());
        }
    }

    private void renderPalFunction(PalFunction value, boolean isSelected, boolean cellHasFocus) {
        append(StringUtil.shortenTextWithEllipsis(value.getId(), 20, 0), new SimpleTextAttributes(SimpleTextAttributes.STYLE_BOLD, JBColor.foreground()), true);
        appendFixedTextFragmentTextWidth(20);
        append(StringUtil.shortenTextWithEllipsis(value.getDescription(), 50, 0), new SimpleTextAttributes(SimpleTextAttributes.STYLE_SMALLER, JBColor.darkGray));
        appendFixedTextFragmentTextWidth(50);
        setToolTipText(value.getDescription());
    }

    private void appendFixedTextFragmentTextWidth(int charsCount) {
        appendFixedTextFragmentWidth(getFontMetrics(getFont()).charWidth('w') * charsCount);
    }
}
