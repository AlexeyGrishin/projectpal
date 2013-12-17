import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.codeInsight.lookup.LookupElementPresentation;
import com.intellij.lang.ASTNode;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.*;
import com.intellij.psi.impl.java.stubs.JavaMethodElementType;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.JavaClassReference;
import com.intellij.psi.impl.source.tree.java.ReferenceListElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class CPTest extends CompletionContributor {
    public CPTest() {

        extend(CompletionType.BASIC, PlatformPatterns.psiElement().afterLeaf(PlatformPatterns.psiElement(JavaTokenType.DOT)), new CompletionProvider<CompletionParameters>() {
            @Override
            protected void addCompletions(@NotNull CompletionParameters completionParameters,
                                          ProcessingContext processingContext,
                                          @NotNull CompletionResultSet completionResultSet) {
                PsiElement position = completionParameters.getPosition();
                System.out.println(position);
                PsiElement source = position.getPrevSibling().getPrevSibling().getPrevSibling();

                if (source instanceof PsiReferenceExpression)
                {
                    System.out.println(source.getLastChild());
                    if (source.getLastChild() instanceof PsiIdentifier)
                    {
                        final PsiIdentifier id = (PsiIdentifier) source.getLastChild();
                        if (id.getText().equalsIgnoreCase("pal"))
                        {
                            completionResultSet.addElement(new LookupElement() {
                                @NotNull
                                @Override
                                public String getLookupString() {
                                    return "hello!";
                                }

                                @Override
                                public void handleInsert(InsertionContext context) {
                                    context.getDocument().replaceString(id.getTextOffset(), context.getTailOffset(), "/*here will be pal code*/");
                                }

                                @Override
                                public void renderElement(LookupElementPresentation presentation) {
                                    super.renderElement(presentation);
                                    presentation.setTailText("упрлс", Color.CYAN);
                                }
                            });

                        }
                    }
                }
            }
        });
    }
}
