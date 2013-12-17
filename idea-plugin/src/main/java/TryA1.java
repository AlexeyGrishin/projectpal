import com.intellij.codeInsight.highlighting.HighlightManager;
import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.DataConstants;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.actionSystem.EditorActionManager;
import com.intellij.openapi.editor.markup.MarkupModel;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.ProjectManager;
import org.jetbrains.annotations.NotNull;


public class TryA1 implements ApplicationComponent {
    public TryA1() {
    }

    public void initComponent() {
        Runnable a = new Runnable() {
            @Override
            public void run() {
                System.out.println("!");
            }
        };
    }

    public void disposeComponent() {
        // TODO: insert component disposal logic here
    }

    @NotNull
    public String getComponentName() {
        return "TryA1";
    }
}
