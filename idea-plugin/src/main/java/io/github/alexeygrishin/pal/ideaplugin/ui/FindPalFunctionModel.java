package io.github.alexeygrishin.pal.ideaplugin.ui;

import com.intellij.ide.util.gotoByName.ChooseByNameBase;
import com.intellij.ide.util.gotoByName.ChooseByNameItemProvider;
import com.intellij.ide.util.gotoByName.SimpleChooseByNameModel;
import com.intellij.lang.Language;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.project.Project;
import com.intellij.util.Processor;
import io.github.alexeygrishin.pal.api.PalFunction;
import io.github.alexeygrishin.pal.ideaplugin.model.PalFunctions;
import io.github.alexeygrishin.pal.ideaplugin.model.PalService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.Collections;
import java.util.List;

public class FindPalFunctionModel extends SimpleChooseByNameModel implements ChooseByNameItemProvider {

    private final PalService service;
    private final PalFunctions snapshot;
    private Language language;

    public FindPalFunctionModel(@NotNull Project project, Language language) {
        super(project, "Describe function you'd like to get", null);
        this.language = language;
        service = PalService.getInstance(project);
        snapshot = service.createSnapshot(language);
    }

    @Override
    public ListCellRenderer getListCellRenderer() {
        //TODO: show description as well
        return new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                return super.getListCellRendererComponent(list, PalFunction.toString(value), index, isSelected, cellHasFocus);
            }
        };
    }

    @Nullable
    @Override
    public String getElementName(Object element) {
        return element instanceof PalFunction ? ((PalFunction) element).getId() : null;
    }

    @Override
    public String[] getNames() {
        //Do not need to return anything because real list of names will be provided by #filterNames method
        //I hope there is better way to reach the same, but I did not found it...
        return new String[0];
    }

    @Override
    protected Object[] getElementsByName(String name, String pattern) {
        //Same, see at #filterElements
        return new Object[0];
    }

    @NotNull
    @Override
    public List<String> filterNames(@NotNull ChooseByNameBase base, @NotNull String[] names, @NotNull String pattern) {
        //it does not matter
        return Collections.emptyList();
    }

    @Override
    public boolean filterElements(@NotNull ChooseByNameBase base, @NotNull String pattern, boolean everywhere, @NotNull ProgressIndicator cancelled, @NotNull Processor<Object> consumer) {
        snapshot.update(pattern);
        cancelled.checkCanceled();
        for (PalFunction function: snapshot.getFunctions()) {
            consumer.process(function);
        }
        //return value always ignored, and there is no comment, so I'm not sure when to return true
        return false;
    }


}
