package io.github.alexeygrishin.pal.ideaplugin.model.file;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import pal.Pal;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IdeaFile implements PhysicalFile {

    private PsiFileLocator locator;

    public IdeaFile(PsiFileLocator locator) {
        this.locator = locator;
    }

    @Override
    public boolean exists() {
        return locator.exists();
    }


    @Override
    public void setContent(final String content) {
        withDocument(new DocAction() {
            @Override
            public void run(Document doc) {
                doc.setText(content);
            }
        });
    }

    private Document getDocument() {
        return PsiDocumentManager.getInstance(getPsiFile().getProject()).getDocument(getPsiFile());
    }

    private PsiFile getPsiFile() {
        return locator.createOrGet();
    }

    private void withDocument(final DocAction action) {
        ApplicationManager.getApplication().invokeAndWait(new Runnable() {
            @Override
            public void run() {
                ApplicationManager.getApplication().runWriteAction(new Runnable() {
                    @Override
                    public void run() {
                        action.run(getDocument());
                    }
                });

            }
        }, ModalityState.defaultModalityState());
    }

    @Override
    public void insertBefore(String marker, final String content) {
        final Pattern makerRegex = Pattern.compile("^(.*" + marker + ".*)$", Pattern.MULTILINE);
        withDocument(new DocAction() {
            @Override
            public void run(Document doc) {
                Matcher m = makerRegex.matcher(doc.getCharsSequence());
                if (m.find()) {
                    int insertHere = m.start() - 1;
                    doc.insertString(insertHere, content);
                }
                else {
                    //TODO: recover somehow - probably reload the whole class
                    throw new IllegalStateException("Cannot find ':addbefore' marker in pal class - do not know where to insert :(");
                }
            }
        });
    }

    @Override
    public String getContent() {
        return ApplicationManager.getApplication().runReadAction(new Computable<String>() {
            @Override
            public String compute() {
                return getDocument().getText();
            }
        });
    }

    @Override
    public String getLanguage() {
        return locator.getLanguage();    //TODO: there shall be valid mapping between idea language names and Pal language names
    }

    @Override
    public String getClassNameToReference() {
        return locator.getClassNameToReference();
    }

    private static interface DocAction {
        public void run(Document doc);
    }
}
