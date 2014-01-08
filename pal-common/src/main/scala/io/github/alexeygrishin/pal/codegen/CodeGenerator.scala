package io.github.alexeygrishin.pal.codegen

import io.github.alexeygrishin.pal.storage.FunctionsStorage
import io.github.alexeygrishin.pal.codegen.langconfig.LangConfig
import io.github.alexeygrishin.pal.codegen.render.CodeRenderer
import io.github.alexeygrishin.pal.codegen.prepare.CodePreparer

/**
 * Entry point for Pal functionality
 * @param storage
 * @param getLangConfig
 */
class CodeGenerator(private val storage: FunctionsStorage, private val getLangConfig: (String) => Option[LangConfig]) {

  private val preparer = new CodePreparer(storage)
  private val renderer = new CodeRenderer

  /**
   * Composes and returns class body for provided language and function names
   * @param langName language name
   * @param funcNames function ids
   * @return class body as string
   */
  def composeClass(langName: String, funcNames: String*) = {
    val langConfig = getLangConfig(langName).getOrElse(throw new UnknownEntityException("language", langName))
    renderer.renderClass(preparer.composeClass(langConfig, funcNames.toList), langConfig)
  }

  //same but composes function body only
  def composeFunction(langName: String, funcName: String) = {
    val langConfig = getLangConfig(langName).getOrElse(throw new UnknownEntityException("language", langName))
    renderer.renderClass(preparer.composeFunction(langConfig, funcName), langConfig)
  }



}


