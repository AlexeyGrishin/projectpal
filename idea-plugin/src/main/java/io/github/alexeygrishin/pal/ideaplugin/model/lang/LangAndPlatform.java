package io.github.alexeygrishin.pal.ideaplugin.model.lang;

/**
 * Provides soome language/platform specific information
 */
public interface LangAndPlatform {
    /**
     *
     * @return language id for Pal server
     */
    String getPalLanguage();

    /**
     * Creates function call expression according to language rules
     * @param functionName name of function (in form of Pal function ID - camelCase)
     * @return function call expression
     */
    FunctionCallString getFunctionCallString(String functionName);
}
