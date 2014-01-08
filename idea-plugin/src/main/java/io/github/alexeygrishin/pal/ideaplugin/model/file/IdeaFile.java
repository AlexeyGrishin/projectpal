package io.github.alexeygrishin.pal.ideaplugin.model.file;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.util.Computable;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import io.github.alexeygrishin.tools.threads.AnyThread;
import io.github.alexeygrishin.tools.threads.InThread;
import io.github.alexeygrishin.tools.threads.UIThread;
import org.jetbrains.annotations.NotNull;

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
    public void setContent(@NotNull final String content) {
        ensurePsiFileExists();
        InThread.execute(new Runnable() {
            @Override
            @UIThread(write = true)
            public void run() {
                getDocument().setText(content);

            }
        });
    }

    private void ensurePsiFileExists() {
        locator.createSync();
    }

    private Document getDocument() {
        return PsiDocumentManager.getInstance(getPsiFile().getProject()).getDocument(getPsiFile());
    }

    private PsiFile getPsiFile() {
        return locator.get();
    }

    @Override
    public void insertBefore(@NotNull String marker, @NotNull final String content) {
        ensurePsiFileExists();
        final Pattern makerRegex = Pattern.compile("^(.*" + marker + ".*)$", Pattern.MULTILINE);
        InThread.execute(new Runnable() {
            @Override
            @UIThread(write = true)
            public void run() {
                Document doc = getDocument();
                Matcher m = makerRegex.matcher(doc.getCharsSequence());
                if (m.find()) {
                    int insertHere = m.start() - 1;
                    doc.insertString(insertHere, content);
                } else {
                    //TODO: recover somehow - probably reload the whole class
                    throw new IllegalStateException("Cannot find ':addbefore' marker in pal class - do not know where to insert :(");
                }
            }
        });
    }

    @NotNull
    @Override
    public String getContent() {
        ensurePsiFileExists();
        return InThread.executeAndReturn(new Computable<String>() {
            @Override
            @AnyThread(read = true)
            public String compute() {
                return getDocument().getText();
            }
        });
    }

    @NotNull
    @Override
    public String getPathAsString() {
        return locator.getPathAsString();
    }

}
