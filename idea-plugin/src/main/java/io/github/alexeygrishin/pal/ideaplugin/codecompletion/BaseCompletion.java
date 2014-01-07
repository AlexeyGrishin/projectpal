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
import io.github.alexeygrishin.pal.ideaplugin.model.*;
import io.github.alexeygrishin.pal.ideaplugin.model.lang.FunctionCallString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BaseCompletion extends CompletionContributor {

    //CompletionUtilCore.DUMMY_IDENTIFIER_TRIMMED is not defined for RubyMine
    private final static String DUMMY = "intellijIdeaRulezzz";

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
        return position.getText().replaceAll(DUMMY, "").trim();
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

            target = target.getPrevSibling();
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
        }
    }

    @Override
    public void fillCompletionVariants(CompletionParameters parameters, CompletionResultSet result) {
        super.fillCompletionVariants(parameters, result);
    }

    public String getPalClassName(Project project, Language language) {
        return PalService.getInstance(project).getPalClass(language).getClassName();
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
            insertFunction(function, context.getProject());
            ApplicationManager.getApplication().runWriteAction(new Runnable() {
                @Override
                public void run() {
                FunctionCallString str = PalService.getInstance(context.getProject()).getFunctionCallString(context.getFile().getLanguage(), function);
                context.getDocument().replaceString(pal.getTextOffset(), context.getTailOffset(), str.functionCall);
                context.getEditor().getCaretModel().moveToOffset(context.getTailOffset() + str.caretOffsetFromEnd);
                }
            });

        }
    }

    private void insertFunction(final PalFunction palFunction, final Project project) {
        ProgressManager.getInstance().executeNonCancelableSection(new Runnable() {
            @Override
            public void run() {
                PalService.getInstance(project).getPalClass().addFunction(palFunction);
            }
        });
    }
}
