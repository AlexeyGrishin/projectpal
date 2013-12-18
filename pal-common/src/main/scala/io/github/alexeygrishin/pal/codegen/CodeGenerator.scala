package io.github.alexeygrishin.pal.codegen

import io.github.alexeygrishin.pal.storage.FunctionsStorage
import io.github.alexeygrishin.pal.codegen.langconfig.LangConfig
import io.github.alexeygrishin.pal.codegen.render.CodeRenderer
import io.github.alexeygrishin.pal.codegen.prepare.CodePreparer

class CodeGenerator(private val storage: FunctionsStorage, private val getLangConfig: (String) => Option[LangConfig]) {

  private val preparer = new CodePreparer(storage)
  private val renderer = new CodeRenderer

  def composeClass(funcName: String, langName: String) = {
    val langConfig = getLangConfig(langName).getOrElse(throw new UnknownEntityException("language", langName))
    renderer.renderClass(preparer.composeClass(funcName, langConfig), langConfig)
  }

  def composeFunction(funcName: String, langName: String) = {
    val langConfig = getLangConfig(langName).getOrElse(throw new UnknownEntityException("language", langName))
    renderer.renderClass(preparer.composeFunction(funcName, langConfig), langConfig)
  }



}


