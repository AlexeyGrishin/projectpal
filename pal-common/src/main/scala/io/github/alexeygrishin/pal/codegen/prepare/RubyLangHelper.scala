package io.github.alexeygrishin.pal.codegen.prepare

import io.github.alexeygrishin.pal.functions.{AType, FunctionImplementation}
import io.github.alexeygrishin.pal.functions.expressions.FunctionRef
import io.github.alexeygrishin.pal.codegen._
import io.github.alexeygrishin.pal.tools.Tool

class RubyLangHelper(private val helper: BuiltinHelper) extends DefaultLangHelper(helper) {
  override def mapType(tp: AType): String = ""  //for ruby we do not need it

  override def getLangName: String = "ruby"

  protected def appendFunctionRef(builder: StringBuilder, ctx: ExpressionParsingContext, fr: FunctionRef) {
    builder.append("&").append(formatFunctionName(fr.name)).append("_lambda")
  }

  override def formatFunctionName(id: String): String = Tool.toUnderscore(id)
}
