package io.github.alexeygrishin.pal.ideaplugin.ui;


import com.intellij.ide.actions.GotoActionBase;
import com.intellij.ide.util.gotoByName.ChooseByNamePopup;
import com.intellij.lang.Language;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import io.github.alexeygrishin.pal.api.PalFunction;
import io.github.alexeygrishin.pal.ideaplugin.model.PalService;
import io.github.alexeygrishin.pal.ideaplugin.model.UserFileManipulator;
import io.github.alexeygrishin.pal.ideaplugin.model.lang.FunctionCallString;
import io.github.alexeygrishin.pal.ideaplugin.remote.PalServerListener;

public class LookupAction extends GotoActionBase {


    @Override
    protected void gotoActionPerformed(final AnActionEvent e) {
        final Editor editor = e.getData(LangDataKeys.EDITOR);
        final Project project = e.getProject();
        final Language language = e.getData(LangDataKeys.LANGUAGE);
        FindPalFunctionModel model = new FindPalFunctionModel(project, language);
        showNavigationPopup(new GotoActionCallback<Object>() {
            @Override
            public void elementChosen(ChooseByNamePopup popup, final Object element) {
                final PalFunction function = (PalFunction) element;
                final PalService service = PalService.getInstance(project);
                final UserFileManipulator userFile = UserFileManipulator.getInstance(project);
                userFile.insertPalFunctionCallAtCaret(editor, service.getPalClass(language), function);
            }
        }, null, ChooseByNamePopup.createPopup(project, model, model));

    }

    @Override
    public void update(AnActionEvent event) {
        boolean isSupported = PalService.isLanguageSupported(event);
        if (!isSupported) {
            event.getPresentation().setEnabledAndVisible(false);
        }
        else if (!PalService.isServerAvailable()) {
            event.getPresentation().setVisible(true);
            event.getPresentation().setEnabled(false);
            event.getPresentation().setDescription("**DISABLED** - please run do not forget to run pal server");
        } else {
            event.getPresentation().setEnabledAndVisible(true);
            super.update(event);
        }
    }


}
