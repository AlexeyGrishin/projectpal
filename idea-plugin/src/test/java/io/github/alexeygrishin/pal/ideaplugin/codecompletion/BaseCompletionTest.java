package io.github.alexeygrishin.pal.ideaplugin.codecompletion;

import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;

public class BaseCompletionTest extends LightCodeInsightFixtureTestCase {


    public void test_it_shall_work_after_pal_with_dot() {
        prepareCode("pal.<caret>");
        assertEquals(Arrays.asList("test 1"), myFixture.getLookupElementStrings());
    }

    private void prepareCode(String expr) {
        myFixture.configureByText("file.java", wrapWithJavaMethod(expr));
        myFixture.complete(CompletionType.BASIC);
    }

    public void test_it_shall_not_work_after_any_else_dot() {
        prepareCode("lap.<caret>");
        assertEquals(Collections.<String>emptyList(), myFixture.getLookupElementStrings());

    }

    private String wrapWithJavaMethod(String expr) {
        return "class A { public static void main(String args[]) { " + expr + "}}";
    }
}
