package io.github.alexeygrishin.pal.codegen

import io.github.alexeygrishin.pal.storage.FunctionsStorage
import io.github.alexeygrishin.pal.codegen.langconfig.LangConfig
import io.github.alexeygrishin.pal.codegen.render.CodeRenderer
import io.github.alexeygrishin.pal.codegen.prepare.CodePreparer

class CodeGenerator(private val storage: FunctionsStorage, private val getLangConfig: (String) => Option[LangConfig]) {

  private val preparer = new CodePreparer(storage)
  private val renderer = new CodeRenderer

  def composeClass(langName: String, funcNames: String*) = {
    val langConfig = getLangConfig(langName).getOrElse(throw new UnknownEntityException("language", langName))
    renderer.renderClass(preparer.composeClass(langConfig, funcNames.toList), langConfig)
  }

  def composeFunction(langName: String, funcName: String) = {
    val langConfig = getLangConfig(langName).getOrElse(throw new UnknownEntityException("language", langName))
    renderer.renderClass(preparer.composeFunction(langConfig, funcName), langConfig)
  }



}


