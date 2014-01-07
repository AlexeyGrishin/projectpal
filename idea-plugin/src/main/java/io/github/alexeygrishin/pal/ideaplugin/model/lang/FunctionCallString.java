package io.github.alexeygrishin.pal.ideaplugin.model.lang;

public class FunctionCallString {
    public FunctionCallString(String functionCall, int caretOffsetFromEnd) {
        this.functionCall = functionCall;
        this.caretOffsetFromEnd = caretOffsetFromEnd;
    }

    public final String functionCall;
    public final int caretOffsetFromEnd;
}
