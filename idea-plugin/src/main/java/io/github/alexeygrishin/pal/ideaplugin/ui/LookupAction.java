package io.github.alexeygrishin.pal.ideaplugin.ui;


import com.intellij.ide.actions.GotoActionBase;

import com.intellij.ide.util.gotoByName.ChooseByNamePopup;

import com.intellij.lang.Language;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.project.Project;
import com.intellij.util.Processor;
import io.github.alexeygrishin.pal.api.PalFunction;
import io.github.alexeygrishin.pal.ideaplugin.model.PalService;
import io.github.alexeygrishin.pal.ideaplugin.model.lang.FunctionCallString;

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
                service.getPalClass(language).addFunction(function);
                ApplicationManager.getApplication().runWriteAction(new Runnable() {
                    @Override
                    public void run() {
                        FunctionCallString str = service.getFunctionCallString(language, function);
                        int caret = editor.getCaretModel().getOffset();
                        editor.getDocument().insertString(caret, str.functionCall);
                        editor.getCaretModel().moveToOffset(caret + str.functionCall.length() + str.caretOffsetFromEnd);
                    }
                });
                //Notifications.Bus.notify(new Notification("pal_group", "Pal class created", "Pal class created!", NotificationType.INFORMATION));
            }
        }, null, ChooseByNamePopup.createPopup(project, model, model));

    }

    @Override
    public void update(AnActionEvent event) {
        boolean isSupported = PalService.isLanguageSupported(event);
        if (!isSupported) {
            event.getPresentation().setEnabledAndVisible(false);
        }
        else {
            event.getPresentation().setEnabledAndVisible(true);
            super.update(event);
        }
    }
}
