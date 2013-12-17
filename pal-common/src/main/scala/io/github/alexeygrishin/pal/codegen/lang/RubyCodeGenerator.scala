package io.github.alexeygrishin.pal.codegen.lang

import io.github.alexeygrishin.pal.functions.{AType, FunctionImplementation}
import io.github.alexeygrishin.pal.functions.expressions.FunctionRef
import io.github.alexeygrishin.pal.codegen._

class RubyLangHelper(private val helper: BuiltinHelper) extends DefaultLangHelper(helper) {
  override def mapType(tp: AType): String = ""  //for ruby we do not need it

  override def getLangName: String = "ruby"

  protected def appendFunctionRef(builder: StringBuilder, ctx: ExpressionParsingContext, fr: FunctionRef) {
    builder.append(":").append(fr.name)
  }
}


class RubyCodeGenerator extends LangGenerator {

  def prepare(function: FunctionImplementation, builtinHelper: BuiltinHelper): RenderableFunction = new RenderableFunction(function, new RubyLangHelper(builtinHelper))

}
