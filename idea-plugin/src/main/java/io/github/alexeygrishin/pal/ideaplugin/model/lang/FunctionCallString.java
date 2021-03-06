package io.github.alexeygrishin.pal.ideaplugin.model.lang;

/**
 * Function call expression and where to locate the caret in the editor (usually
 *  value is -1 - to locate caret between parenthesises)
 */
public class FunctionCallString {
    public FunctionCallString(String functionCall, int caretOffsetFromEnd) {
        this.functionCall = functionCall;
        this.caretOffsetFromEnd = caretOffsetFromEnd;
    }

    public final String functionCall;
    public final int caretOffsetFromEnd;
}
