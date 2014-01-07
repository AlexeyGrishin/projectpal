package io.github.alexeygrishin.pal.ideaplugin.ui;

import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.BooleanTableCellEditor;
import com.intellij.ui.BooleanTableCellRenderer;
import com.intellij.ui.components.JBTextField;
import com.intellij.ui.table.TableView;
import com.intellij.util.ui.ListTableModel;
import io.github.alexeygrishin.pal.api.PalFunction;
import io.github.alexeygrishin.pal.ideaplugin.model.*;
import io.github.alexeygrishin.pal.ideaplugin.remote.PalServerListener;
import io.github.alexeygrishin.tools.ui.SimpleColumn;
import io.github.alexeygrishin.tools.ui.TableCell;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Deprecated
//TODO: delete
public class PalPanel {}
/*
public class PalPanel implements ToolWindowFactory, Condition<Project> {

    static class FunctionInfo {
        private boolean included;
        private PalFunction function;
        private FunctionsContainer container;

        FunctionInfo(boolean included, PalFunction function, FunctionsContainer container) {
            this.included = included;
            this.function = function;
            this.container = container;
        }

        public boolean isIncluded() {
            return included;
        }

        public void setIncluded(boolean included) {
            container.setIncluded(function, included);
        }

        public String getName() {
            return function.getId();
        }

        public boolean isEditable() {
            return container.canInclude(function);
        }
    }


    @TableCell(
            name = "Included",
            renderWith = BooleanTableCellRenderer.class,
            editWith = BooleanTableCellEditor.class,
            width = 32)
    static class IncludedColumn extends SimpleColumn<FunctionInfo, Boolean> {


        @Nullable
        @Override
        public Boolean valueOf(FunctionInfo functionInfo) {
            return functionInfo.included;
        }

        @Override
        public void setValue(FunctionInfo functionInfo, Boolean value) {
            functionInfo.setIncluded(value);
        }

        @Override
        public boolean isCellEditable(FunctionInfo functionInfo) {
            return functionInfo.isEditable();
        }
    }

    @TableCell(name = "Name")
    static class NameColumn extends SimpleColumn<FunctionInfo, String> {
        @Nullable
        @Override
        public String valueOf(FunctionInfo functionInfo) {
            return functionInfo.getName();
        }
    }


    static class FunctionsModel extends ListTableModel<FunctionInfo> implements FunctionsContainerListener {


        private FunctionsContainer localSource;
        private FunctionsContainer externalSource;

        FunctionsModel(FunctionsContainer localSource, FunctionsContainer externalSource) {
            super(
                    new IncludedColumn(),
                    new NameColumn()
            );
            this.localSource = localSource;
            this.externalSource = externalSource;
            localSource.addListener(this);
            externalSource.addListener(this);
        }

        @Override
        public void onChangingStart(FunctionsContainer source) {

        }

        @Override
        public void onChangingEnd(FunctionsContainer source) {
            ApplicationManager.getApplication().invokeLater(new Runnable() {
                @Override
                public void run() {
                    setItems(getMergedList());
                }
            });
        }

        private List<FunctionInfo> getMergedList() {
            Collection<PalFunction> local = localSource.getFunctions();
            Collection<PalFunction> external = externalSource.getFunctions();
            List<FunctionInfo> infos = new ArrayList<FunctionInfo>(local.size() + external.size());
            for (PalFunction localFunction: local) {
                infos.add(new FunctionInfo(true, localFunction, localSource));
            }
            for (PalFunction externalFunction: external) {
                if (!local.contains(externalFunction)) {
                    infos.add(new FunctionInfo(false, externalFunction, localSource));
                }
            }
            return infos;
        }


    }

    public static class PalToolWindow implements PalServiceListener, PalServerListener {
        private final CardLayout cards;
        private PalService service;
        private Project project;
        private ToolWindow toolWindow;

        private JPanel initializing;
        private JPanel error;
        private JPanel main;
        private JPanel container;

        public PalToolWindow(ToolWindow toolWindow, Project project, PalService service) {
            this.toolWindow = toolWindow;
            this.project = project;
            this.service = service;
            service.addListener(this);
            service.addServerListener(this);
            cards = new CardLayout();
            container = new JPanel(cards);
            JComponent toolWindowPanel = toolWindow.getComponent();
            toolWindowPanel.setLayout(new BorderLayout());
            toolWindowPanel.add(container, BorderLayout.CENTER);
            container.add(initializing = createIntializingPanel(), "init");
            container.add(error = createErrorPanel(), "error");
            container.add(main = createMainPanel(), "main");
            show("init");
        }

        private void show(String view) {
            cards.show(container, view);
        }

        private JPanel createMainPanel() {

            final PalServerFunctions snapshot = service.getPalFunctions();
            ListTableModel<FunctionInfo> model = new FunctionsModel(service.getPalClass(), snapshot);
            final TableView<FunctionInfo> table = new TableView<FunctionInfo>(model);
            final JBTextField field = new JBTextField();
            field.addKeyListener(new KeyAdapter() {
                @Override
                public void keyReleased(KeyEvent e) {
                    ApplicationManager.getApplication().executeOnPooledThread(new Runnable() {
                        @Override
                        public void run() {
                            snapshot.update(field.getText(), "java");  //TODO: language
                        }
                    });
                }
            });

            ActionToolbar bar = ActionManager.getInstance().createActionToolbar("toolbar1", (ActionGroup) ActionManager.getInstance().getAction("pal_group"), true);
            JPanel root = new JPanel();
            root.setLayout(new BorderLayout());
            root.add(bar.getComponent(), BorderLayout.NORTH);
            JPanel panel = new JPanel(new BorderLayout());
            root.add(panel, BorderLayout.CENTER);
            panel.add(field, BorderLayout.NORTH);
            panel.add(table, BorderLayout.CENTER);
            snapshot.addListener(new FunctionsContainerListener() {
                @Override
                public void onChangingStart(FunctionsContainer source) {
                    ApplicationManager.getApplication().invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            table.setEnabled(false);
                        }
                    });
                }

                @Override
                public void onChangingEnd(FunctionsContainer source) {
                    //TODO: wrapper for different application threads
                    ApplicationManager.getApplication().invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            table.setEnabled(true);
                        }
                    });
                }
            });
            return root;
        }

        private JLabel errorDetails = new JLabel();

        private JPanel createErrorPanel() {
            JPanel panel = new JPanel();
            BoxLayout box = new BoxLayout(panel, BoxLayout.Y_AXIS);
            panel.setLayout(box);
            panel.add(new JLabel("Error: "));
            panel.add(errorDetails);
            panel.add(new JButton(new AbstractAction("Retry") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    ApplicationManager.getApplication().executeOnPooledThread(new Runnable() {
                        @Override
                        public void run() {
                            service.ping();
                   }
                });
                }

            }));
            return panel;
        }

        private JPanel createIntializingPanel() {
            JPanel panel = new JPanel();
            panel.add(new JLabel("Loading..."));
            return panel;
        }

        @Override
        public void onConnectionStart() {

        }

        @Override
        public void onConnectionEnd() {

        }

        @Override
        public void onConnectionFail(final String error) {
            ApplicationManager.getApplication().invokeLater(new Runnable() {
                @Override
                public void run() {
                    errorDetails.setText(error);
                    show("error");
                }});
        }

        @Override
        public void onConnectionRestore() {
            ApplicationManager.getApplication().invokeLater(new Runnable() {
                @Override
                public void run() {
                    show("main");
                }
            });
        }

        @Override
        public void onInitialized() {
            ApplicationManager.getApplication().invokeLater(new Runnable() {
                @Override
                public void run() {
                    show("main");
                }});
        }
    }

    @Override
    public void createToolWindowContent(Project project, ToolWindow toolWindow) {
        PalToolWindow window = new PalToolWindow(toolWindow, project, PalService.getInstance(project));

    }

    @Override
    public boolean value(Project project) {
        return true;
    }
}*/
