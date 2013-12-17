import com.intellij.codeInsight.highlighting.HighlightManager;
import com.intellij.codeInsight.navigation.NavigationGutterIconRenderer;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.colors.ColorKey;
import com.intellij.openapi.editor.colors.EditorFontType;
import com.intellij.openapi.editor.event.EditorFactoryAdapter;
import com.intellij.openapi.editor.event.EditorFactoryEvent;
import com.intellij.openapi.editor.markup.*;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.actions.ActiveAnnotationGutter;
import com.intellij.util.ui.ColorIcon;
import org.intellij.lang.annotations.JdkConstants;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.plaf.metal.MetalIconFactory;
import java.awt.*;
import java.util.*;
import java.util.List;


public class Try1 implements ProjectComponent {
    private final Project project;

    public Try1(Project project) {
        this.project = project;
    }

    public void initComponent() {
        //FileEditorManager manager = FileEditorManager.getInstance(project);
        //Editor editor = manager.getSelectedTextEditor();
        //final HighlightManager highlightManager =
        //        HighlightManager.getInstance(project);
        //highlightManager.addOccurrenceHighlight(editor, 10, 20, new TextAttributes(null, Color.blue, null, EffectType.WAVE_UNDERSCORE, 0), HighlightManager.HIDE_BY_TEXT_CHANGE, null, null);
        EditorFactory.getInstance().addEditorFactoryListener(new EditorFactoryAdapter() {
            @Override
            public void editorCreated(@NotNull final EditorFactoryEvent event) {
                ApplicationManager.getApplication().invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        if (event.getEditor().getDocument().getLineCount() < 10) return;
                        RangeHighlighter hh = event.getEditor().getMarkupModel().addRangeHighlighter(event.getEditor().getDocument().getLineStartOffset(3)+2, event.getEditor().getDocument().getLineStartOffset(10)+2, HighlighterLayer.GUARDED_BLOCKS, new TextAttributes(null, Color.CYAN, null, EffectType.WAVE_UNDERSCORE, Font.PLAIN), HighlighterTargetArea.EXACT_RANGE);
                        hh.setGutterIconRenderer(new GutterIconRenderer() {
                            @NotNull
                            @Override
                            public Icon getIcon() {
                                return new MetalIconFactory.FolderIcon16();
                            }

                            @Override
                            public boolean equals(Object o) {
                                return false;
                            }

                            @Override
                            public int hashCode() {
                                return 4;
                            }

                            @Nullable
                            @Override
                            public String getTooltipText() {
                                return "Hello1!!!!" ;
                            }

                            @Override
                            public Alignment getAlignment() {
                                return Alignment.RIGHT;
                            }
                        });

                    }
                });
                event.getEditor().getGutter().registerTextAnnotation(new ActiveAnnotationGutter() {
                    @Override
                    public void doAction(int i) {

                    }

                    @Override
                    public Cursor getCursor(int i) {
                        return Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
                    }

                    @Nullable
                    @Override
                    public String getLineText(int i, Editor editor) {
                        return i == 3 ? "hello!" : "";
                    }

                    @Nullable
                    @Override
                    public String getToolTip(int i, Editor editor) {
                        return "";
                    }

                    @Override
                    public EditorFontType getStyle(int i, Editor editor) {
                        return EditorFontType.PLAIN;
                    }

                    @Nullable
                    @Override
                    public ColorKey getColor(int i, Editor editor) {
                        return null;
                    }

                    @Nullable
                    @Override
                    public Color getBgColor(int i, Editor editor) {
                        return null;
                    }

                    @Override
                    public List<AnAction> getPopupActions(int i, Editor editor) {
                        return Collections.emptyList();
                    }

                    @Override
                    public void gutterClosed() {

                    }
                });
            }
        }, new Disposable() {
                                                                 @Override
                                                                 public void dispose() {

                                                                 }
                                                             });

    }

    public void disposeComponent() {
        // TODO: insert component disposal logic here
    }

    @NotNull
    public String getComponentName() {
        return "Try1";
    }

    public void projectOpened() {
        // called when project is opened
    }

    public void projectClosed() {
        // called when project is being closed
    }
}
