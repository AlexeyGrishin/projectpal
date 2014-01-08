package io.github.alexeygrishin.pal.ideaplugin.codecompletion;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementPresentation;
import com.intellij.lang.Language;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.util.Consumer;
import com.intellij.util.ProcessingContext;
import io.github.alexeygrishin.pal.api.PalFunction;
import io.github.alexeygrishin.pal.ideaplugin.model.LanguageNotSupportedByPal;
import io.github.alexeygrishin.pal.ideaplugin.model.PalFunctions;
import io.github.alexeygrishin.pal.ideaplugin.model.PalService;
import io.github.alexeygrishin.pal.ideaplugin.model.UserFileManipulator;
import io.github.alexeygrishin.pal.ideaplugin.model.lang.FunctionCallString;
import io.github.alexeygrishin.pal.ideaplugin.remote.PalServerError;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Pattern;

/**
 * Base class for all code completions. Every new language may use the same class or create a subclass.
 * This code completion is called after user typed something like 'pal.'. Methods from Pal server
 * shall be added after already existent Pal class methods.
 */
public class BaseCompletion extends CompletionContributor {

    //CompletionUtilCore.DUMMY_IDENTIFIER_TRIMMED is not defined for RubyMine
    //... and in RubyMine it is started from capital I...
    private final static Pattern DUMMY = Pattern.compile("intellijIdeaRulezzz",Pattern.CASE_INSENSITIVE);

    public BaseCompletion() {
        this(true);
    }

    protected BaseCompletion(boolean doBaseExtend) {
        if (doBaseExtend)
            baseExtend();
    }

    private void baseExtend() {
        extend(CompletionType.BASIC,
                PlatformPatterns.psiElement().afterLeaf("."),
                new CompletionProvider<CompletionParameters>() {
                    @Override
                    protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull final CompletionResultSet result) {
                        PsiElement pal;
                        if ((pal = getPalBeforeDot(parameters.getPosition())) == null) return;
                        result.runRemainingContributors(parameters,  new Consumer<CompletionResult>() {
                            @Override
                            public void consume(CompletionResult completionResult) {
                                result.consume(completionResult.getLookupElement());
                            }
                        }, false);
                        addCompletion(result, pal, getLookupString(parameters.getPosition()), parameters.getOriginalFile().getProject(), parameters.getOriginalFile().getLanguage());
                    }
                });
    }

    private String getLookupString(PsiElement position) {
        return DUMMY.matcher(position.getText()).replaceAll("").trim();
    }

    public static PsiElement getPalBeforeDot(PsiElement element) {
        PsiElement dot = findBefore(element, ".", 2);
        if (dot == null) return null;
        return findBefore(dot, "pal", 2);
    }


    @Nullable
    public static PsiElement findBefore(PsiElement element, String text, int howFar) {
        PsiElement target = element;
        while (howFar --> 0 && target != null && !target.getText().equalsIgnoreCase(text)) {
            PsiElement next = target.getPrevSibling();
            if (next == null && target.getParent() != null) {
                next = target.getParent().getPrevSibling();
            }
            target = next;
        }
        if (target == null || !target.getText().equalsIgnoreCase(text)) return null;
        return target;
    }

    private void addCompletion(final CompletionResultSet result, PsiElement pal, String lookupString, Project project, final Language language) {
        try {
            PalService service = PalService.getInstance(project);
            PalFunctions snapshot = service.createSnapshot(language);
            snapshot.update(lookupString);
            for (final PalFunction function : snapshot.getFunctions()) {
                result.addElement(new PalFunctionLookupElement(function, pal));
            }
        } catch (LanguageNotSupportedByPal languageNotSupportedByPal) {
            result.addLookupAdvertisement("The language '" + language + "' is not supported by Pal yet");
        } catch (PalServerError connectionError) {
            //just do nothing
        }
    }

    private class PalFunctionLookupElement extends LookupElement {
        private final PalFunction function;
        private PsiElement pal;

        public PalFunctionLookupElement(PalFunction function, PsiElement pal) {
            this.function = function;
            this.pal = pal;
        }

        @NotNull
        @Override
        public String getLookupString() {
            return function.getId();
        }

        @Override
        public void renderElement(LookupElementPresentation presentation) {
            presentation.setItemText(function.getId());
            presentation.appendTailText("   " + function.getDescription(), true);
        }


        @Override
        public void handleInsert(final InsertionContext context) {
            UserFileManipulator userFile = UserFileManipulator.getInstance(context.getProject());
            userFile.insertPalFunctionCall(context.getEditor(),                    PalService.getInstance(context.getProject()).getPalClass(context.getFile().getLanguage()),
                    function, pal.getTextOffset(), context.getTailOffset());
        }
    }

}
